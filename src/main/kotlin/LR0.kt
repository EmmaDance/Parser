
fun closure(grammar: Grammar, state:State):State{
    if (state.isEmpty()) return state
    val items = state.copy().items
    val originalItems = state.items
    var modified = true
    while(modified){
        modified = false
        for (item in originalItems){
            val symbol = item.getSymbolAfterDot()
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
    }
//    println("closure($state)=$items")
    return State(items)
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
    while(modified){
        modified = false
        for (state in collection) {
            for (symbol in symbols) {
                val goto = goto(grammar,state,symbol)
                if (!goto.isEmpty() &&
                    goto !in collection){
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
        action[state.number] = state.action(grammar)
        symbols.forEach{symbol->
            goto[Pair(state.number,symbol)]=goto(grammar,state,symbol).number
        }

    }

    return LR0_Table(action,goto,states)
}

