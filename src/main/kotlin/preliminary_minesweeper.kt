import kotlin.random.Random

const val MINE = "X"
const val SAFE = "."
const val DIMEN = 9
fun main() {
    var height = DIMEN
    var width = DIMEN
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
    //fill in number values for MINE-adjacent cells
    for((i,row) in grid.withIndex()) {
        for ((j, cell) in row.withIndex()){
            var counterMines = 0
            if (i == 0) {
                if (j == 0) {
                    val sliceBelow = grid[i + 1][j]
                    if (sliceBelow == "X") counterMines++
                    val sliceRight = grid[i][j + 1]
                    if (sliceRight == "X") counterMines++
                    val sliceDiag = grid[i + 1][j + 1]
                    if (sliceDiag == "X") counterMines++
                } else if (j == DIMEN - 1) {
                    val sliceBelow = grid[i + 1][j]
                    if (sliceBelow == "X") counterMines++
                    val sliceLeft = grid[i][j - 1]
                    if (sliceLeft == "X") counterMines++
                    val sliceDiag = grid[i + 1][j - 1]
                    if (sliceDiag == "X") counterMines++
                } else {
                    val sliceBelow = grid[i + 1].slice(j-1..j+1)
                    val belowCount = sliceBelow.count { it == "X"}
                    repeat(belowCount) { counterMines++ }
                    val sliceRight = grid[i][j + 1]
                    if (sliceRight == "X") counterMines++
                    val sliceLeft = grid[i][j - 1]
                    if (sliceLeft == "X") counterMines++
                }
            } else if (i == DIMEN - 1) {
                if (j == 0) {
                    if (grid[DIMEN - 2][0] == "X") counterMines++
                    if (grid[DIMEN - 1][1] == "X") counterMines++
                    if (grid[DIMEN - 2][1] == "X") counterMines++
                } else if (j == DIMEN -1) {
                    if (grid[DIMEN - 2][DIMEN - 1] == "X") counterMines++
                    if (grid[DIMEN - 1][DIMEN - 2] == "X") counterMines++
                    if (grid[DIMEN - 2][DIMEN - 2] == "X") counterMines++
                } else {
                    val sliceAbove = grid[i - 1].slice(j - 1..j + 1)
                    repeat(sliceAbove.count{ it == "X"}) {counterMines++}
                    if (grid[i][j - 1] == "X") counterMines++
                    if (grid[i][j + 1] == "X") counterMines++
                }
            } else {
                if (j == 0) {
                    val sliceAbove = grid[i - 1].slice(j..j + 1)
                    repeat(sliceAbove.count { it == "X" }) { counterMines++ }
                    val sliceBelow = grid[i + 1].slice(j..j + 1)
                    repeat(sliceBelow.count { it == "X" }) { counterMines++ }
                    val cellRight = grid[i][j + 1]
                    if (cellRight == "X") counterMines++
                } else if (j == DIMEN - 1) {
                    val sliceAbove = grid[i - 1].slice(j - 1..j)
                    repeat(sliceAbove.count { it == "X" }) { counterMines++ }
                    val sliceBelow = grid[i + 1].slice(j - 1..j)
                    repeat(sliceBelow.count { it == "X" }) { counterMines++ }
                    val cellLeft = grid[i][j - 1]
                    if (cellLeft == "X") counterMines++
                } else {
                    val sliceAbove = grid[i - 1].slice(j - 1..j + 1)
                    repeat(sliceAbove.count { it == "X" }) { counterMines++ }
                    val sliceBelow = grid[i + 1].slice(j - 1..j + 1)
                    repeat(sliceBelow.count { it == "X" }) { counterMines++ }
                    val cellLeft = grid[i][j - 1]
                    if (cellLeft == "X") counterMines++
                    if (grid[i][j + 1] == "X") counterMines++
                }
            }
            if (grid[i][j] != "X" && counterMines > 0) {
                grid[i][j] = counterMines.toString()
            }
        }
    }
    for (row in grid) {
        println(row.joinToString(""))
    }
}