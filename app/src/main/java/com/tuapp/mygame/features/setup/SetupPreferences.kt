package com.tuapp.mygame.features.setup

data class SetupPreferences(
    val alias: String = "",
    val gridSize: Int = 5,
    val trackTime: Boolean = false
) {
    val isComplete: Boolean get() = alias.isNotBlank()
}
