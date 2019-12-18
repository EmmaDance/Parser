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
        if(getSymbolsAfterDot().contains("")) { // . is last
            // check for reduce-reduce conflicts
            getSymbolsAfterDot().forEach {
                if (it.isNotEmpty())
                    throw Exception("Shift-reduce conflict")
            }
            if (items.distinct()[0].lhs=="S'")
                return "a"
            return "r" // + production number
        }
        return "e"
    }

}