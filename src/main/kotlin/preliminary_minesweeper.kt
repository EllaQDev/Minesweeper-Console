import kotlin.random.Random

const val MINE = "$"
const val SAFE = "."

fun main() {
    var height = 7
    var width = 7
    val random = Random.nextBoolean()
    val grid = List(height){ MutableList<String>(width){
        if (Random.nextBoolean()) MINE else SAFE}
    }

    // first attempt to print grid -- prints whole sequence multiple times
//    val sequence = grid.flatten().joinToString("").asSequence()
//    repeat(height) {
//        println(sequence.chunked(width).toList().joinToString(""))
//    }

// second attempt to print grid -- straightforward
    for (row in grid) {
        println(row.joinToString(""))
    }

}