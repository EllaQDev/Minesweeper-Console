import kotlin.random.Random

const val MINE = "X"
const val SAFE = "."
const val MARKER = "*"
const val DIMEN = 9

fun main() {
    val minesweeper = Minesweeper()
    minesweeper.launchGame()
}

enum class Status(var value: String) {
    HIDDEN_MINE(SAFE),
    SAFE_CELL(SAFE),
    MARKED_MINE(MARKER),
    REVEALED_MINE(MINE),
    MINE_ADJ_CELL("")
}

class Minesweeper {
data class Cell( var status: Status) {
    override fun equals(other: Any?): Boolean {
        if (other !is Cell) return false
        return other.status == this.status
    }

    override fun hashCode(): Int {
        return status.ordinal.hashCode() * 7 + 13 + status.value.hashCode() * 7 + 13
    }

    override fun toString(): String {
        return status.value
    }
}
    val height = DIMEN
    val width = DIMEN
    val grid = List(height){ MutableList<Cell>(width){
        Cell(Status.SAFE_CELL)}
    }
    var playerGrid : List<MutableList<Cell>>? = null
    fun launchGame() {
        val numMines = promptNumMines()
        createMines(numMines)
        determineNumsOnGrid()
        //printGMGrid()
        createPlayerGrid()
        printPlayerGrid()
    }

    fun promptNumMines(): Int {
        println("How many mines do you want on the field?")
        return readln().toInt()
    }
    fun createMines(mines: Int) {
        var xCount = 0
        while (xCount < mines){
            val targetCellX = Random.nextInt(0, 9)
            val targetCellY = Random.nextInt(0, 9)
            if (grid[targetCellX][targetCellY].status != Status.REVEALED_MINE) {
                grid[targetCellX][targetCellY].status = Status.REVEALED_MINE
                xCount++
            }
        }
    }

    fun determineNumsOnGrid() {
        for((i,row) in grid.withIndex()) {
            for ((j, cell) in row.withIndex()){
                var counterMines = 0
                if (i == 0) {
                    if (j == 0) {
                        val sliceBelow = grid[i + 1][j]
                        if (sliceBelow.status == Status.REVEALED_MINE) counterMines++
                        val sliceRight = grid[i][j + 1]
                        if (sliceRight.status == Status.REVEALED_MINE) counterMines++
                        val sliceDiag = grid[i + 1][j + 1]
                        if (sliceDiag.status == Status.REVEALED_MINE) counterMines++
                    } else if (j == DIMEN - 1) {
                        val sliceBelow = grid[i + 1][j]
                        if (sliceBelow.status == Status.REVEALED_MINE) counterMines++
                        val sliceLeft = grid[i][j - 1]
                        if (sliceLeft.status == Status.REVEALED_MINE) counterMines++
                        val sliceDiag = grid[i + 1][j - 1]
                        if (sliceDiag.status == Status.REVEALED_MINE) counterMines++
                    } else {
                        val sliceBelow = grid[i + 1].slice(j-1..j+1)
                        val belowCount = sliceBelow.count { it.status == Status.REVEALED_MINE}
                        repeat(belowCount) { counterMines++ }
                        val sliceRight = grid[i][j + 1]
                        if (sliceRight.status == Status.REVEALED_MINE) counterMines++
                        val sliceLeft = grid[i][j - 1]
                        if (sliceLeft.status == Status.REVEALED_MINE) counterMines++
                    }
                } else if (i == DIMEN - 1) {
                    if (j == 0) {
                        if (grid[DIMEN - 2][0].status == Status.REVEALED_MINE) counterMines++
                        if (grid[DIMEN - 1][1].status == Status.REVEALED_MINE) counterMines++
                        if (grid[DIMEN - 2][1].status == Status.REVEALED_MINE) counterMines++
                    } else if (j == DIMEN -1) {
                        if (grid[DIMEN - 2][DIMEN - 1].status == Status.REVEALED_MINE) counterMines++
                        if (grid[DIMEN - 1][DIMEN - 2].status == Status.REVEALED_MINE) counterMines++
                        if (grid[DIMEN - 2][DIMEN - 2].status == Status.REVEALED_MINE) counterMines++
                    } else {
                        val sliceAbove = grid[i - 1].slice(j - 1..j + 1)
                        repeat(sliceAbove.count{ it.status == Status.REVEALED_MINE}) {counterMines++}
                        if (grid[i][j - 1].status == Status.REVEALED_MINE) counterMines++
                        if (grid[i][j + 1].status == Status.REVEALED_MINE) counterMines++
                    }
                } else {
                    if (j == 0) {
                        val sliceAbove = grid[i - 1].slice(j..j + 1)
                        repeat(sliceAbove.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val sliceBelow = grid[i + 1].slice(j..j + 1)
                        repeat(sliceBelow.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val cellRight = grid[i][j + 1]
                        if (cellRight.status == Status.REVEALED_MINE) counterMines++
                    } else if (j == DIMEN - 1) {
                        val sliceAbove = grid[i - 1].slice(j - 1..j)
                        repeat(sliceAbove.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val sliceBelow = grid[i + 1].slice(j - 1..j)
                        repeat(sliceBelow.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val cellLeft = grid[i][j - 1]
                        if (cellLeft.status == Status.REVEALED_MINE) counterMines++
                    } else {
                        val sliceAbove = grid[i - 1].slice(j - 1..j + 1)
                        repeat(sliceAbove.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val sliceBelow = grid[i + 1].slice(j - 1..j + 1)
                        repeat(sliceBelow.count { it.status == Status.REVEALED_MINE }) { counterMines++ }
                        val cellLeft = grid[i][j - 1]
                        if (cellLeft.status == Status.REVEALED_MINE) counterMines++
                        if (grid[i][j + 1].status == Status.REVEALED_MINE) counterMines++
                    }
                }
                if (grid[i][j].status != Status.REVEALED_MINE && counterMines > 0) {
                    val outputCell = Cell(Status.MINE_ADJ_CELL)
                    outputCell.status.value = counterMines.toString()
                    grid[i][j] = outputCell
                }
            }
        }
    }
    fun createPlayerGrid(){
        playerGrid = grid
        playerGrid!!.forEach { it.forEach {
            if (it.status == Status.REVEALED_MINE) {it.status = Status.HIDDEN_MINE}
        }}
    }

    fun printGMGrid() {
        for (row in grid) {
            println(row.joinToString(""))
        }
    }
    fun printPlayerGrid() {
        println(" |123456789|")
        println("_|_________|")
        for ((i,row) in playerGrid!!.withIndex()) {
            println("${i+1}|${row.joinToString("")}|")
        }
    }
}