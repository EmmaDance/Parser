data class State (val items: MutableSet<Item>){
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

    fun action(grammar: Grammar):String{
        println(getSymbolsAfterDot())
        if(getSymbolsAfterDot().contains("")) { // . is last => reduce or accept action
            val productionNumber = items.distinct()[0].productionNumber
            // check for reduce-reduce conflicts
            if(items.size>1){
                items.forEach {
                    if (it.productionNumber != productionNumber)
                        throw Exception("Reduce-reduce conflict!")
                }
            }
            getSymbolsAfterDot().forEach {
                if (it.isNotEmpty())
                    throw Exception("Shift-reduce conflict")
            }
            if (items.distinct()[0].lhs=="S'")
                return "a"
            return "r$productionNumber" // + production number
        }
        // check for shift
        return "e"
    }

}