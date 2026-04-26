// GameTimer.kt
package com.tuapp.mygame

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameTimer(
    private val durationSeconds: Long = 120L,
    private val scope: CoroutineScope,
    private val onTimeUp: () -> Unit
) {
    private var timerJob: Job? = null

    private val _timeLeft = MutableStateFlow(durationSeconds)
    val timeLeft = _timeLeft.asStateFlow()

    private val _isTimeUp = MutableStateFlow(false)
    val isTimeUp = _isTimeUp.asStateFlow()

    fun start() {
        timerJob?.cancel()
        _timeLeft.value = durationSeconds
        _isTimeUp.value = false
        timerJob = scope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
            _isTimeUp.value = true
            onTimeUp()
        }
    }

    fun cancel() {
        timerJob?.cancel()
    }

    fun reset() {
        timerJob?.cancel()
        _timeLeft.value = durationSeconds
        _isTimeUp.value = false
    }
}