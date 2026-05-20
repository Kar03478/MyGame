package com.tuapp.mygame.features.game.presentation

import com.tuapp.mygame.features.game.logic.GameTimer
import com.tuapp.mygame.features.game.logic.isGameOver
import com.tuapp.mygame.features.game.logic.mergePieces
import com.tuapp.mygame.features.game.model.CakePiece
import com.tuapp.mygame.features.game.model.CakeType
import com.tuapp.mygame.features.game.model.CellState
import com.tuapp.mygame.features.game.model.EndReason
import com.tuapp.mygame.features.game.model.GameLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameViewModel : ViewModel() {
    companion object {
        const val TARGET_SCORE = 200
    }

    private val _rows = MutableStateFlow(5)
    val rows = _rows.asStateFlow()

    private val _cols = MutableStateFlow(5)
    val cols = _cols.asStateFlow()

    private val _alias = MutableStateFlow("")
    val alias = _alias.asStateFlow()

    private val _trackTime = MutableStateFlow(false)
    val trackTime = _trackTime.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver = _isGameOver.asStateFlow()
    private val _hasWon = MutableStateFlow(false)
    val hasWon = _hasWon.asStateFlow()
    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _cells = MutableStateFlow(emptyGrid(5, 5))
    val cells = _cells.asStateFlow()

    private val _tray = MutableStateFlow(newTray())
    val tray = _tray.asStateFlow()

    private var gameStartTime = 0L


    private val timer = GameTimer(
        durationSeconds = 120L,
        scope = viewModelScope,
        onTimeUp = { _isGameOver.value = true }
    )

    val timeLeft = timer.timeLeft
    val isTimeUp = timer.isTimeUp

    fun startGame(playerAlias: String, newRows: Int, newCols: Int, withTimer: Boolean = false) {
        _alias.value = playerAlias
        _rows.value = newRows
        _cols.value = newCols
        _trackTime.value = withTimer
        _score.value = 0
        _hasWon.value = false
        _cells.value = emptyGrid(newRows, newCols)
        _tray.value  = newTray()
        _isGameOver.value = false
        gameStartTime     = System.currentTimeMillis()

        if (withTimer) timer.start() else timer.reset()
    }

    fun placePiece(trayIndex: Int, row: Int, col: Int) {
        if (_isGameOver.value || _hasWon.value) return

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
            applyPoints(result.pointsScored)

            viewModelScope.launch {
                delay(350)
                _cells.value = _cells.value.map { it.copy(isDisappearing = false) }
                _tray.value = _tray.value.toMutableList().also {
                    it[trayIndex] = randomPiece()
                }
                if (!_hasWon.value) {
                    _isGameOver.value = isGameOver(_cells.value)
                }
            }
        } else {
            _cells.value  = result.grid
            applyPoints(result.pointsScored)
            _tray.value = _tray.value.toMutableList().also {
                it[trayIndex] = randomPiece()
            }
            if (!_hasWon.value) {
                _isGameOver.value = isGameOver(result.grid)
            }
        }
    }

    private fun applyPoints(points: Int) {
        _score.value += points
        if (_score.value >= TARGET_SCORE) {
            _hasWon.value = true
            _isGameOver.value = false
            timer.cancel()
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
        if (_trackTime.value) timer.start() else timer.reset()
        _score.value = 0
        _hasWon.value = false
        _isGameOver.value = false
        _cells.value = emptyGrid(_rows.value, _cols.value)
        _tray.value = newTray()
    }

    fun abandonGame() {
        timer.reset()
        _score.value = 0
        _hasWon.value = false
        _isGameOver.value = false
        _cells.value = emptyGrid(_rows.value, _cols.value)
        _tray.value = newTray()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
    fun buildGameLog(): GameLog {
        val elapsed = (System.currentTimeMillis() - gameStartTime) / 1000
        return GameLog(
            alias = _alias.value,
            score = _score.value,
            gridSize = _rows.value,
            durationSeconds = elapsed,
            endReason = if (_hasWon.value) {
                EndReason.WIN
            } else {
                if (isTimeUp.value) EndReason.TIME_UP else EndReason.BOARD_FULL
            }
        )
    }

}
