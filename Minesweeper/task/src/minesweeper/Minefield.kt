package minesweeper

import kotlin.random.Random

/**
 * Class representing the field of the game
 *
 * @param size the size of the game. It corresponds to the length of the side
 * of the field
 * @param numberOfMines the number of mines in the field
 */
class Minefield(private val size: Int, private val numberOfMines: Int) {

    /**
     * A map from positions on this field to the corresponding cell
     */
    val playingField = buildMap {
        for (row in 1..this@Minefield.size) {
            for (col in 1..this@Minefield.size) {
                put(Pair(row, col), Cell(row, col))
            }
        }
    }

    /**
     * Whether the game is lost or not
     */
    private var lost = false

    /**
     * Whether the game is waiting for the first exploring play
     */
    private var firstExploring = true

    /**
     * Puts numberOfMines mines on the field avoiding the first position
     *
     * @param noPosition the position to avoid
     */
    private fun initMines(noPosition: Pair<Int, Int>) {
        var mines = 0
        while (mines < numberOfMines) {
            val row = Random.nextInt(1, size + 1)
            val col = Random.nextInt(1, size + 1)
            val position = Pair(row, col)
            if (position == noPosition) continue
            val cell = playingField[position]!!
            if (cell.isMine()) continue
            cell.putMine()
            mines++
        }
    }

    /**
     * @return whether the game has been won
     */
    fun gameWon(): Boolean {
        return playingField.values.all {
            it.isMine() || it.isExplored()
        }
    }

    /**
     * Sets the game to lost
     */
    fun lose() {
        lost = true
    }

    /**
     * Checks whether a position has been explored
     *
     * @param position the position to check
     */
    fun isExplored(position: Pair<Int, Int>): Boolean {
        val (row, col) = position
        check(row in 1..size && col in 1..size)
        val cell = playingField[position]!!
        return cell.isExplored()
    }

    /**
     * Toggles the marked status of the cell of a position
     *
     * @param position the position
     */
    fun toggleMark(position: Pair<Int, Int>) {
        val (row, col) = position
        check(row in 1..size && col in 1..size)
        val cell = playingField[position]!!
        cell.toggleMark()
    }

    /**
     * Checks whether a position has a mine
     *
     * @param position the position to check
     */
    fun isMine(position: Pair<Int, Int>): Boolean {
        val (row, col) = position
        check(row in 1..size && col in 1..size)
        val cell = playingField[position]!!
        return cell.isMine()
    }

    /**
     * Explores a position, setting it to explored state.
     * If no mines are nearby all surrounding positions are explored recursively
     *
     * @param position the position to explore
     */
    fun explore(position: Pair<Int, Int>) {
        val (row, col) = position
        check(row in 1..size && col in 1..size)
        if (firstExploring) {
            initMines(position)
            firstExploring = false
        }
        val cell = playingField[position]!!
        cell.explore()
        if (cell.minesCloseBy == 0) {
            for (neighbourPos in cell.neighbourPositions) {
                if (!playingField[neighbourPos]!!.isExplored()) explore(neighbourPos)
            }
        }
    }

    /**
     * Prints the current state of the game
     */
    fun printState() {
        print(" |")
        for (i in 1..size) print(i)
        println("|")
        print("—|")
        for (i in 1..size) print("—")
        println("|")
        for (row in 1..size) {
            print("$row|")
            for (col in 1..size) {
                val cell = playingField[Pair(row, col)]!!
                print(cell.getSymbol())
            }
            println("|")
        }
        print("—|")
        for (i in 1..size) print("—")
        println("|")
    }

    /**
     * Class representing a cell of the Minefield
     *
     * @param row the row where this cell is situated
     * @param col the column where this cell is situated
     */
    inner class Cell(row: Int, col: Int) {

        /**
         * Whether this cell has a mine
         */
        private var mined = false

        /**
         * How many mines are surrounding this cell
         */
        var minesCloseBy = 0
            private set

        /**
         * Whether this cell is marked
         */
        private var marked = false

        /**
         * Whether this cell has been explored
         */
        private var explored = false

        /**
         * List of the surrounding positions of this cell
         */
        val neighbourPositions = buildList {
            for (i in row - 1..row + 1) {
                for (j in col - 1..col + 1) {
                    if (i in 1..this@Minefield.size
                        && j in 1..this@Minefield.size) add(Pair(i, j))
                }
            }
        }

        /**
         * Checks if this cell has a mine
         */
        fun isMine() = mined

        /**
         * Puts a mine on this cell
         */
        fun putMine() {
            mined = true
            notifyNeighboursImMine()
        }

        /**
         * Notifies all surrounding cells tha this cell has a mine
         */
        private fun notifyNeighboursImMine() {
            for (neighbour in neighbourPositions) {
                playingField[neighbour]!!.newMineClose()
            }
        }

        /**
         * Increases the number of mines surrounding this cell
         */
        private fun newMineClose() {
            minesCloseBy++
        }

        /**
         * Toggles the marked status of this cell
         */
        fun toggleMark() {
            marked = !marked
        }

        /**
         * Checks if this cell has been explored
         */
        fun isExplored() = explored

        /**
         * Sets the status of this cell to explored
         */
        fun explore() {
            explored = true
        }

        /**
         * Gets the symbol of this cell for printing the field state
         * * "X" for a mine
         * * "*" for unexplored and marked
         * * "." for unexplored and unmarked
         * * "/" for explored with no surrounding mines
         * * "n" for explored with n surrounding mines
         */
        fun getSymbol(): String {
            return if (lost && isMine()) "X"
            else if (!isExplored() && marked) "*"
            else if (!isExplored()) "."
            else if (minesCloseBy == 0) "/"
            else minesCloseBy.toString()
        }

    }

}
