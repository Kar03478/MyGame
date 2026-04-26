package com.tuapp.mygame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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

    private val timer = GameTimer(
        durationSeconds = 120L,
        scope = viewModelScope,
        onTimeUp = { _isGameOver.value = true }
    )

    val timeLeft = timer.timeLeft
    val isTimeUp = timer.isTimeUp

    fun startGame(playerAlias: String, newRows: Int, newCols: Int, withTimer: Boolean = false) {
        alias.value = playerAlias
        rows.value  = newRows
        cols.value  = newCols
        trackTime.value = withTimer
        _score.value = 0
        _cells.value = emptyGrid(newRows, newCols)
        _tray.value  = newTray()
        _isGameOver.value = false
        if (withTimer) timer.start() else timer.reset()
    }

    fun placePiece(trayIndex: Int, row: Int, col: Int) {
        val piece = _tray.value.getOrNull(trayIndex) ?: return
        val cell  = _cells.value.find { it.row == row && it.col == col }
        if (cell?.piece != null) return

        val gridWithPiece = _cells.value.map {
            if (it.row == row && it.col == col) it.copy(piece = piece) else it
        }

        val result = mergePieces(gridWithPiece, row, col)
        if (result.disappearingCells.isNotEmpty()) {
            _cells.value = result.grid.map { cell ->
                if (result.disappearingCells.contains(cell.row to cell.col))
                    cell.copy(isDisappearing = true)
                else cell
            }
            _score.value += result.pointsScored

            viewModelScope.launch {
                delay(350)
                _cells.value = _cells.value.map { it.copy(isDisappearing = false) }
                _tray.value = _tray.value.toMutableList().also {
                    it[trayIndex] = randomPiece()
                }
                _isGameOver.value = isGameOver(_cells.value)
            }
        } else {
            _cells.value  = result.grid
            _score.value += result.pointsScored
            _tray.value = _tray.value.toMutableList().also {
                it[trayIndex] = randomPiece()
            }
            _isGameOver.value = isGameOver(result.grid)
        }
    }

    private fun emptyGrid(r: Int, c: Int) =
        List(r * c) { i -> CellState(i / c, i % c) }

    private fun newTray() = List(3) { randomPiece() }

    private fun randomPiece() = CakePiece(
        type   = CakeType.entries.random(),
        slices = (1..3).random()
    )
    fun resetGame() {
        if (trackTime.value) timer.start() else timer.reset()
        _score.value = 0
        _isGameOver.value = false
        _cells.value = emptyGrid(rows.value, cols.value)
        _tray.value = newTray()
    }

    fun abandonGame() {
        timer.reset()
        _score.value = 0
        _isGameOver.value = false
        _cells.value = emptyGrid(rows.value, cols.value)
        _tray.value = newTray()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}

