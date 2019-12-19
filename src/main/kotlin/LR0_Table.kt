data class LR0_Table(val action:HashMap<Int,String>, val goto: HashMap<Pair<Int, String>, Int>, val states: MutableSet<State>)
