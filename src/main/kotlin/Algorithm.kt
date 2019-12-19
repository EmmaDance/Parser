import java.lang.Exception
import java.util.*

class Algorithm (val table:LR0_Table, val grammar: Grammar, val input:Stack<String>){

    val workStack:Stack<Pair<String,Int>> = Stack()
    val inputStack: Stack<String> = input
    val outputStack: Stack<Int> = Stack()

    fun start(){
        workStack.push(Pair("$",0))
        while(true){
            val pair = workStack.peek()
            val stateNumber = pair.second
            when (table.action[stateNumber]?.first){
                "s" -> {
                    val symbol = inputStack.pop()
                    val nextStateNumber = table.goto[Pair(stateNumber, symbol)] ?: -1
                    if(nextStateNumber==-1) {
                        println("The sequence is not accepted")
                        return
                    }
                    workStack.push(Pair(symbol,nextStateNumber))

                }
                "r" ->{
                    val productionNumber = table.action[stateNumber]?.second!!
                    val production = grammar.getProduction(productionNumber)
                    for ( i in production.rhs.size-1 until 0){
                        println(i)
                        workStack.pop()
                    }
                    outputStack.push(productionNumber)
                    val lastStateNumber = workStack.peek().second
                    val nextStateNumber = table.goto[Pair(lastStateNumber,production.lhs)] ?: -1
                    if(nextStateNumber==-1) {
                        println("The sequence is not accepted")
                        return
                    }
                    workStack.push(Pair(production.lhs,nextStateNumber))

                }
                "acc"->{
                    println("Sequence accepted")
                    return
                }
                "e"->{
                    throw Exception("ERROR!")
                }
            }

        }

    }


}