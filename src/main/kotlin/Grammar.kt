
data class Grammar(
    val non_terminals: MutableSet<String>,
    val terminals: MutableSet<String>,
    val productions: MutableSet<Production>,
    val starting_symbol: String){

    fun getProduction(number:Int):Production{
        return productions.filter {
            it.number == number
        }[0]
    }

    fun isRegular():Boolean {

        if (!this.isRightLinear())
            return false

        // check if there are epsilon productions
        var hasEpsilon = false
        productions.forEach{
            if (it.rhs.contains("eps")){
                hasEpsilon = true
                if (it.lhs != starting_symbol)
                    return false
            }
        }

        // checks if the starting symbol appears in the rhs
        productions.forEach{
            it.rhs.forEach{ s ->
                if(starting_symbol in s && hasEpsilon)
                    return false
            }
        }
        return true
    }

    private fun isRightLinear(): Boolean {
        productions.forEach {
            // wrong length
            if (it.rhs.size > 2)
                return false
            // if the production has a single non_terminal
            if(it.rhs.size == 1&&it.rhs[0] in non_terminals)
                return false
            if(it.rhs.size == 2){
                val first: String = it.rhs[0]
                val second: String = it.rhs[1]
                if (first in non_terminals || second in terminals)
                    return false
            }
        }
        return true
    }
}