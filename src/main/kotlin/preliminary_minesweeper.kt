import kotlin.random.Random

const val MINE = "X"
const val SAFE = "."

fun main() {
    var height = 9
    var width = 9
    println("How many mines do you want on the field?")
    val numMines = readln().toInt()
    //val random = Random.nextBoolean()
    val grid = List(height){ MutableList<String>(width){
        SAFE}
    }
    var xCount = 0
    while (xCount < numMines){
        val targetCellX = Random.nextInt(0, 9)
        val targetCellY = Random.nextInt(0, 9)
        if (grid[targetCellX][targetCellY] != MINE) {
            grid[targetCellX][targetCellY] = MINE
            xCount++
        }
    }
    for (row in grid) {
        println(row.joinToString(""))
    }
}