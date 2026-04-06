package com.tuapp.mygame

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class GameViewModel : ViewModel() {
    val rows = MutableStateFlow(5)
    val cols = MutableStateFlow(5)

    private val _cells = MutableStateFlow(emptyGrid(5, 5))
    val cells = _cells.asStateFlow()

    private val _tray = MutableStateFlow(newTray())
    val tray = _tray.asStateFlow()

    fun resizeGrid(newRows: Int, newCols: Int) {
        rows.value = newRows
        cols.value = newCols
        _cells.value = emptyGrid(newRows, newCols)
    }

    fun placePiece(trayIndex: Int, row: Int, col: Int) {
        if (_cells.value.all { it.piece != null }) return
        val piece = _tray.value.getOrNull(trayIndex) ?: return

        // only can put it when a cell is empty
        val cell = _cells.value.find { it.row == row && it.col == col }
        if (cell?.piece != null) return

        _cells.value = _cells.value.map {
            if (it.row == row && it.col == col) it.copy(piece = piece) else it
        }
        _tray.value = _tray.value.toMutableList().also {
            it[trayIndex] = randomPiece()
        }
    }

    private fun emptyGrid(r: Int, c: Int) =
        List(r * c) {
                i -> CellState(i / c, i % c)
        }

    private fun newTray() = List(3) {
        randomPiece()
    }

    private fun randomPiece() = CakePiece(
        type   = CakeType.entries.random(),
        slices = (1..3).random()
    )

}

