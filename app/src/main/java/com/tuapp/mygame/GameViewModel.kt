package com.tuapp.mygame

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GameViewModel : ViewModel() {

    val rows = MutableStateFlow(5)
    val cols = MutableStateFlow(5)
    val alias = MutableStateFlow("")
    val trackTime = MutableStateFlow(false)

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver = _isGameOver.asStateFlow()
    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _cells = MutableStateFlow(emptyGrid(5, 5))
    val cells = _cells.asStateFlow()

    private val _tray = MutableStateFlow(newTray())
    val tray = _tray.asStateFlow()

    fun startGame(playerAlias: String, newRows: Int, newCols: Int) {
        alias.value = playerAlias
        rows.value  = newRows
        cols.value  = newCols
        _cells.value = emptyGrid(newRows, newCols)
        _tray.value  = newTray()
    }

    fun placePiece(trayIndex: Int, row: Int, col: Int) {
        val piece = _tray.value.getOrNull(trayIndex) ?: return
        val cell  = _cells.value.find { it.row == row && it.col == col }
        if (cell?.piece != null) return

        val gridWithPiece = _cells.value.map {
            if (it.row == row && it.col == col) it.copy(piece = piece) else it
        }

        val result = mergePieces(gridWithPiece, row, col)

        _cells.value  = result.grid
        _score.value += result.pointsScored

        _tray.value = _tray.value.toMutableList().also {
            it[trayIndex] = randomPiece()
        }
        _isGameOver.value = isGameOver(result.grid)
    }

    private fun emptyGrid(r: Int, c: Int) =
        List(r * c) { i -> CellState(i / c, i % c) }

    private fun newTray() = List(3) { randomPiece() }

    private fun randomPiece() = CakePiece(
        type   = CakeType.entries.random(),
        slices = (1..3).random()
    )
    fun resetGame() {
        _score.value = 0
        _isGameOver.value = false
        _cells.value = emptyGrid(rows.value, cols.value)
        _tray.value = newTray()
    }
}

