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
import com.tuapp.mygame.features.db.GameLogDao
import com.tuapp.mygame.features.db.GameLogEntity
import com.tuapp.mygame.features.game.model.GameEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameViewModel() : ViewModel() {
    companion object {
        const val TARGET_SCORE = 200
        private const val GAME_DURATION_SECONDS = 120L
    }
    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events = _events.asStateFlow()

    private var dao: GameLogDao? = null
    private var hasInsertedFinalLog = false
    private var hasOpenedResults = false

    private val _finalLog = MutableStateFlow<GameLog?>(null)
    val finalLog = _finalLog.asStateFlow()

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

    fun setDao(dao: GameLogDao) {
        this.dao = dao
    }
    fun saveGameLog() {
        val log = snapshotFinalLog()
        val logDao = dao ?: return
        if (hasInsertedFinalLog) return
        hasInsertedFinalLog = true

        viewModelScope.launch {
            logDao.insert(
                GameLogEntity(
                    alias = log.alias,
                    score = log.score,
                    rows = _rows.value,
                    cols = _cols.value,
                    durationSeconds = log.durationSeconds,
                    result = log.endReason.name
                )
            )
        }
    }

    private fun addEvent(message: String) {
        _events.value = _events.value + GameEvent(message = message)
    }
    private val timer = GameTimer(
        durationSeconds = GAME_DURATION_SECONDS,
        scope = viewModelScope,
        onTimeUp = {
            if (!_hasWon.value && !_isGameOver.value) {
                _isGameOver.value = true
                addEvent("Tiempo agotado")
            }
        }
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
        _finalLog.value = null
        hasInsertedFinalLog = false
        hasOpenedResults = false
        if (withTimer) timer.start() else timer.reset()
        _events.value = emptyList()
        addEvent("Partida iniciada: $playerAlias")
    }

    fun shouldOpenResults(): Boolean {
        snapshotFinalLog()
        if (hasOpenedResults) return false
        hasOpenedResults = true
        return true
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
                    val ended = isGameOver(_cells.value)
                    _isGameOver.value = ended
                    if (ended) addEvent("Tablero lleno")
                }
            }
        } else {
            _cells.value  = result.grid
            applyPoints(result.pointsScored)
            _tray.value = _tray.value.toMutableList().also {
                it[trayIndex] = randomPiece()
            }
            if (!_hasWon.value) {
                val ended = isGameOver(result.grid)
                _isGameOver.value = ended
                if (ended) addEvent("Tablero lleno")
            }
        }
    }

    private fun applyPoints(points: Int) {
        if (points > 0) addEvent("Pieza fusionada: +$points pts")
        else addEvent("Pieza colocada")
        _score.value += points
        if (_score.value >= TARGET_SCORE) {
            _hasWon.value = true
            _isGameOver.value = false
            timer.cancel()
            addEvent("Victoria")
        }
    }

    private fun emptyGrid(r: Int, c: Int) =
        List(r * c) { i -> CellState(i / c, i % c) }

    private fun newTray() = List(3) { randomPiece() }

    private fun randomPiece() = CakePiece(
        type   = CakeType.entries.random(),
        slices = (1..3).random()
    )
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
    private fun snapshotFinalLog(): GameLog {
        return _finalLog.value ?: buildGameLog().also { _finalLog.value = it }
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
