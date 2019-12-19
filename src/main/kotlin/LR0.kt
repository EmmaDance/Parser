import java.io.File
import java.util.*
import kotlin.collections.HashSet

fun closure(grammar: Grammar, state:State):State{
//    println(state)
    if (state.isEmpty()) return state
    var items = state.copy().items
    var originalItems = state.copy().items
    var modified = true
    while(modified){
        modified = false
        for (item in originalItems){
            val symbol = item.getSymbolAfterDot()
//            println(symbol)
            grammar.productions.filter {
                it.lhs == symbol }.forEach{ // productions of this symbol
                val lhs = it.lhs // the left hand side of the newly created item
                val list = ArrayList<String>()
                list.add(".")
                it.rhs.forEach { rhp ->
                    list.add(rhp) // add all the symbols to the right side of the item
                }
                val newItem = Item(lhs, list, it.number)
                if (newItem !in items){
                    items.add(newItem)
                    modified = true
                }
            }
        }
        originalItems = items.deepcopy()
    }
    return State(items)
}

private fun MutableSet<Item>.deepcopy(): MutableSet<Item> {
    val items = HashSet<Item>()
    this.forEach {
        val newItem: Item = it.copy()
        items.add(newItem)
    }
    return items
}

fun goto(grammar: Grammar, state: State, symbol:String):State{
    val newItems = HashSet<Item>()
    state.items.forEach {
        val newItem = it.copy() // deepcopy
        if (newItem.rhs.contains(symbol)){
            val dotIndex = newItem.rhs.indexOf(".")
            if (newItem.rhs.size>dotIndex+1) { // make sure the dot is not the last element
                if(newItem.rhs[dotIndex+1]==symbol){
                    newItem.rhs[dotIndex] = symbol
                    newItem.rhs[dotIndex+1] = "."
                    newItems.add(newItem)
                }
            }
        }
    }

    return closure(grammar, State(newItems))
}

fun canonicalCollection(grammar: Grammar): MutableSet<State>{
    val collection = HashSet<State>()
    val rhs: MutableList<String> = ArrayList()
    rhs.add(".")
    rhs.add(grammar.starting_symbol)
    var items = HashSet<Item>()
    items.add(Item("S'",rhs,0))
    val s0 = closure(grammar,State(items))
    collection.add(s0)
    var modified = true
    val symbols = grammar.non_terminals
    symbols.addAll(grammar.terminals)
    val newCollection = HashSet<State>()
    var count = 0
    while(modified){
        modified = false
        for (state in collection) {
            for (symbol in symbols) {
                val goto = goto(grammar,state,symbol)
                if (!goto.isEmpty() &&
                    goto !in collection){
                    count ++
                    goto.number = count
                    newCollection.add(goto)
                    modified = true
                }
            }
        }
        collection.addAll(newCollection)
        newCollection.clear()
    }
    return collection
}

fun buildTable(grammar: Grammar):LR0_Table {
    val action: HashMap<Int, Pair<String,Int>> = HashMap()
    val goto: HashMap<Pair<Int, String>, Int> = HashMap()
    val states: MutableSet<State> = canonicalCollection(grammar)
    val symbols = grammar.non_terminals
    symbols.addAll(grammar.terminals)
    states.forEach { state ->
//        println(state)
        action[state.number] = state.action(grammar)
        //println("action "+ state.action(grammar).toString())
        symbols.forEach{symbol->
            goto[Pair(state.number,symbol)]=getStateNumberFromCC(states, goto(grammar, state, symbol))
            //println("goto " + goto(grammar,state,symbol).number.toString())
        }

    }

    return LR0_Table(action,goto,states)
}

fun getStateNumberFromCC(collection: MutableSet<State>, state: State) : Int{
    val states: List<State> = collection.filter{
        it == state
    }
    //println("STATES: " + states)
    if (states.size > 0) {
        return states[0].number
    }
    return -1
}

fun readInput(fileName: String): Stack<String> {
    val stack: Stack<String> = Stack()
    File(fileName).forEachLine {
        stack.push(it.trim())
    }
    val result: Stack<String> = Stack()
    while(stack.isNotEmpty()){
        result.push(stack.pop())
    }
    return result
}