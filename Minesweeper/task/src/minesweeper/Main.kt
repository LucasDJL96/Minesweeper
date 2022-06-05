package minesweeper

/**
 * Main method. Controls the flow of the game
 */
fun main() {
    println("How many mines do you want on the field?")
    val mines = readln().toInt()
    val minefield = Minefield(9, mines)
    minefield.printState()
    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val (col, row, action) = readln().split(" ")
        val position = Pair(row.toInt(), col.toInt())
        if (action == "mine") {
            if (minefield.isExplored(position)) {
                println("Already explored!")
                continue
            } else {
                minefield.toggleMark(position)
            }
        } else if (action == "free") {
            if (minefield.isMine(position)) {
                minefield.lose()
                minefield.printState()
                println("You stepped on a mine and failed!")
                return
            } else {
                minefield.explore(position)
            }
        } else {
            println("Action not recognized.")
            continue
        }
        minefield.printState()
        if (minefield.gameWon()) {
            println("Congratulations! You found all the mines!")
            return
        }
    }
}
