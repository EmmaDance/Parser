import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

data class State (val items: MutableSet<Item>){

    val number:Int = crtNumber

    init {
        crtNumber++
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as State

        if (items!=other.items) return false

        return true
    }


    override fun hashCode(): Int{
        return items.toTypedArray().contentHashCode()
    }

    fun getSymbolsAfterDot(): MutableList<String>{
        val symbols: MutableList<String> = ArrayList()
        for (item in items) {
            symbols.add(item.getSymbolAfterDot())
        }
        return symbols
    }

    fun isEmpty(): Boolean = items.isEmpty()

    fun copy():State{
        val newItems: MutableSet<Item> = HashSet()
        for (item in items) {
            newItems.add(item.copy())
        }
        return State(newItems)
    }

    fun action(grammar: Grammar):Pair<String,Int>{
        println(getSymbolsAfterDot())
        if(getSymbolsAfterDot().contains("")) { // . is last => reduce or accept action
            val productionNumber = items.distinct()[0].productionNumber
            if(items.size>1){
                getSymbolsAfterDot().forEach {
                    if (it.isNotEmpty())
                        throw Exception("Shift-reduce conflict in state "+ this.toString())
                }
                // check for reduce-reduce conflicts
                items.forEach {
                    if (it.productionNumber != productionNumber)
                        throw Exception("Reduce-reduce conflict  in state " + this.toString())
                }
            }
            if (items.distinct()[0].lhs=="S'")
                return Pair("a",-1)
            return Pair("r",productionNumber) // + production number
        }
        // check for shift
        if(!getSymbolsAfterDot().contains(""))
            return Pair("s",-1)
        return Pair("e",-1)
    }

    companion object {
        var crtNumber: Int = 0
    }

}