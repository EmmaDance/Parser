import java.lang.Exception
import java.util.*

class Algorithm (private val table:LR0_Table, private val grammar: Grammar, input:Stack<String>){

    private val workStack:Stack<Pair<String,Int>> = Stack()
    private val inputStack: Stack<String> = input
    private val outputStack: Stack<Int> = Stack()

    fun start(){
        workStack.push(Pair("$",0))
        while(true){
            val pair = workStack.peek()
            val stateNumber = pair.second
            println(workStack)
            println(inputStack)
            println(outputStack)
            when (table.action[stateNumber]?.first){
                "s" -> {
                    println("s")
                    val symbol = inputStack.pop()
                    val nextStateNumber = table.goto[Pair(stateNumber, symbol)] ?: -1
                    if(nextStateNumber==-1) {
                        println("The sequence is not accepted - shift - no goto for $stateNumber and $symbol")
                        return
                    }
                    workStack.push(Pair(symbol,nextStateNumber))

                }
                "r" ->{
                    println("r")
                    val productionNumber = table.action[stateNumber]?.second!!
//                    println("production number: " + productionNumber)
                    val production = grammar.getProduction(productionNumber)
//                    println("production : " + production)
                    for ( i in production.rhs.size-1 downTo 0){
                        workStack.pop()
                    }
                    outputStack.push(productionNumber)
                    val lastStateNumber = workStack.peek().second
                    val nextStateNumber = table.goto[Pair(lastStateNumber,production.lhs)] ?: -1
                    if(nextStateNumber==-1) {
                        println("The sequence is not accepted - reduce - no goto for $lastStateNumber and ${production.lhs}")
                        return
                    }
                    workStack.push(Pair(production.lhs,nextStateNumber))

                }
                "a"->{
                    println("Sequence accepted")
                    return
                }
                "e"->{
                    throw Exception("ERROR!")
                }
                else ->{
                    println ("WHAT IS GOING ON?")
                    println(table.action[stateNumber]?.first)
                    return
                }
            }

        }

    }


}