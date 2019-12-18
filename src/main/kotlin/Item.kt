data class Item (val lhs: String, val rhs: MutableList<String>, val productionNumber: Int){
    fun getSymbolAfterDot(): String{
        val dotIndex = rhs.indexOf(".")
        if(rhs.size>dotIndex+1)
            return rhs[dotIndex+1]
        else
            return ""
    }

    fun copy():Item {
        val newRhs: MutableList<String> = ArrayList()
        rhs.forEach{
            newRhs.add(it)
        }
        return Item(lhs,newRhs, productionNumber)
    }

}