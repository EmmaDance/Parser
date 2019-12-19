data class LR0_Table(val action:HashMap<Int, Pair<String,Int>>, val goto: HashMap<Pair<Int, String>, Int>, val states: MutableSet<State>)
