import kotlin.random.Random

const val MINE = "X"
const val SAFE = "."
const val MARKER = "*"
const val DIMEN = 9
const val HASH = "/"

fun main() {
    val minesweeper = Minesweeper()
    minesweeper.launchGame()
}

enum class Status(var value: String) {
    HIDDEN_MINE(SAFE),
    SAFE_CELL(SAFE),
    MARKED_MINE(MARKER),
    REVEALED_MINE(MINE),
    MINE_ADJ_CELL(""),
    MARKED_SAFE(HASH),
    MASKED(SAFE)
}

class Minesweeper {
data class Cell(val row: Int, val col: Int, var status: Status, var adjMines : Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (other !is Cell) return false
        return other.status == this.status && other.row == this.row && other.col == this.col
    }

    override fun hashCode(): Int {
        return status.ordinal.hashCode() * 7 + 13 + status.value.hashCode() * 7 + 13 + row + col
    }

    override fun toString(): String {
        return if (status != Status.MINE_ADJ_CELL) status.value else adjMines.toString()
    }
}
    val height = DIMEN
    val width = DIMEN
    val grid = List(height){ row -> MutableList<Cell>(width){
        col -> Cell(row + 1, col + 1, Status.SAFE_CELL)}
    }
    var playerGridKey : List<MutableList<Cell>>? = null
    var playerGrid : List<MutableList<Cell>>? = null
    var won = false
    var lost = false
    val traversedCells = mutableSetOf<Cell>()
    fun launchGame() {
        val numMines = promptNumMines()
        createMines(numMines)
        determineNumsOnGrid()
        //printGMGrid()
        createPlayerGrids()
        printPlayerGrid()
        while(!won && !lost) {
            promptUser()
        }
        if (!lost) println("Congratulations! You found all the mines!")
    }

    fun promptUser() {
        println("Set/delete mine marks (x and y coordinates):")
        val input = readln().split(" ")
        val col = input[0].toInt()
        val row = input[1].toInt()
        val command = input[2]
        //val currStatus = playerGrid!![row - 1][col - 1].status
        if (command == "mine") {

            if (playerGrid!![row - 1][col - 1].status == Status.MINE_ADJ_CELL) {
                println("There is a number here!")
                printPlayerGrid()
                return
            } else {
                if (playerGrid!![row - 1][col - 1].status != Status.MARKED_MINE) {
                    playerGrid!![row - 1][col - 1].status = Status.MARKED_MINE
                } else {
                    playerGrid!![row - 1][col - 1].status = Status.SAFE_CELL
                }
                printPlayerGrid()
            }
            won = checkWinCondition()
            won = checkWinConditionSafeCells()
        } else {
            //command is "free"
            if (playerGridKey!![row - 1][col - 1].status == Status.HIDDEN_MINE) {
                println("You stepped on a mine and failed!")
                lost = true
            } else if (playerGridKey!![row - 1][col - 1].status == Status.MINE_ADJ_CELL) {
                playerGrid!![row - 1][col - 1].status = Status.MINE_ADJ_CELL
            } else if (playerGridKey!![row - 1][col - 1].status == Status.SAFE_CELL) {
                propagateSafe(row - 1, col - 1)
            }
            won = checkWinConditionSafeCells()
            printPlayerGrid()
        }
    }

    //may need a variable holding cells that have been checked
    //and a function to preface propagateSafe that empties the list of checked cells
    //or maybe add line to propagate nesting that if MARKED_SAFE return?
    private fun propagateSafe(row: Int, col: Int) {
//        if (inRow - 1 < 0) return
//        if (inCol - 1 < 0) return
//        if (inRow > DIMEN ) return
//        if (inCol > DIMEN) return
//        val row = inRow - 1
//        val col = inCol - 1
        if (traversedCells.contains(playerGrid!![row][col])) return
        playerGrid!![row][col].status = Status.MARKED_SAFE
        traversedCells.add(playerGrid!![row][col])
        if (row - 1 >= 0) {
            if (playerGridKey!![row - 1][col].status == Status.SAFE_CELL) {
                propagateSafe(row - 1, col)
            } else if (playerGridKey!![row - 1][col].status == Status.MINE_ADJ_CELL) {
                playerGrid!![row - 1][col].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (row - 1 >= 0 && col - 1 >= 0) {
            val currCell = playerGridKey!![row - 1][col - 1]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row - 1, col - 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row - 1][col - 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (col - 1 >= 0) {
            val currCell = playerGridKey!![row][col - 1]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row, col - 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row][col - 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (col - 1 >= 0 && row + 1 <= DIMEN - 1) {
            val currCell = playerGridKey!![row + 1][col - 1]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row + 1, col - 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row + 1][col - 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (row + 1 <= DIMEN - 1) {
            val currCell = playerGridKey!![row + 1][col]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row + 1, col)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row + 1][col].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (row + 1 <= DIMEN -1 && col + 1 <= DIMEN - 1) {
            val currCell = playerGridKey!![row + 1][col + 1]
            //if (traversedCells.contains(currCell)) return
            if (currCell.status == Status.SAFE_CELL) {

                propagateSafe(row + 1, col + 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row + 1][col + 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (col + 1 <= DIMEN - 1) {
            val currCell = playerGridKey!![row][col + 1]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row, col + 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row][col + 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
        if (col + 1 <= DIMEN - 1 && row - 1 >= 0) {
            val currCell = playerGridKey!![row - 1][col + 1]
            if (currCell.status == Status.SAFE_CELL) {
                propagateSafe(row - 1, col + 1)
            } else if (currCell.status == Status.MINE_ADJ_CELL) {
                playerGrid!![row - 1][col + 1].status = Status.MINE_ADJ_CELL
            } else {}
        }
    }

    private fun checkWinConditionSafeCells(): Boolean {
        if (playerGridKey!!.flatten().count() - playerGridKey!!.flatten().count {
                it.status == Status.MINE_ADJ_CELL || it.status == Status.HIDDEN_MINE
            }
            == playerGrid!!.flatten().filter { it.status == Status.MARKED_SAFE}.count()
            ) return true
        return false
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
                //println(grid[targetCellX][targetCellY].status)
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
                    //println(counterMines)
//                    val outputCell =
//                    outputCell.status.value = counterMines.toString()
//                    println("$outputCell ${outputCell.col} ${outputCell.row}")
                    grid[i][j] = Cell(i + 1, j + 1, Status.MINE_ADJ_CELL, counterMines)
//                    grid[i][j].status.value = counterMines.toString()
//                    println(counterMines)
                }
            }
        }
    }
    fun createPlayerGrids(){
        playerGridKey = grid.map { row ->
            row.map {
                it.copy()
            }.toMutableList()
        }
        //printPlayerGrid()
        playerGridKey!!.forEach { it.forEach {
            if (it.status == Status.REVEALED_MINE) {it.status = Status.HIDDEN_MINE}
        }}
        playerGrid = grid.map { row ->
            row.map {
                it.copy()
            }.toMutableList()
        }
        playerGrid!!.forEach { it.forEach {
            it.status = Status.MASKED
        }}
        //printPlayerGrid()
        //printGMGrid()
    }

    fun printGMGrid() {
        for (row in grid) {
            println(row.joinToString(""))
        }
    }
    fun printPlayerGridKey() {
        //playerGrid!!.forEach {it.forEach { println( "${it.status.value} ${it.col} ${it.row}")} }
        println(" |123456789|")
        println("-|---------|")
        for ((i,row) in playerGridKey!!.withIndex()) {
            println("${i+1}|${row.joinToString("")}|")
        }
        println("-|---------|")
    }

    fun printPlayerGrid() {
        println(" |123456789|")
        println("-|---------|")
        for ((i,row) in playerGrid!!.withIndex()) {
            println("${i+1}|${row.joinToString("")}|")
        }
        println("-|---------|")
    }

    fun checkWinCondition(): Boolean {
        val allMines = grid.flatten().filter { it.status == Status.REVEALED_MINE}
        // println(allMines)
        val markedMines = playerGridKey!!.flatten().filter { it.status == Status.MARKED_MINE }
        // println(markedMines)
        if (allMines.count() != markedMines.count()) return false
        val allCoords = allMines.map { it.col to it.row}
        val markedCoords = markedMines.map { it.col to it.row}
        // println("allCoords: $allCoords")
        // println("markedCoords: $markedCoords")
        if (allCoords == markedCoords) return true
        return false
    }
}

