package com.tuapp.mygame.logic

import com.tuapp.mygame.model.CellState
import com.tuapp.mygame.model.TOTAL_PLATE_SLICES

data class MergeResult(
    val grid: List<CellState>,
    val pointsScored: Int,
    val disappearingCells: Set<Pair<Int, Int>>
)

fun mergePieces(grid: List<CellState>, row: Int, col: Int): MergeResult {
    val currentPiece = grid.find { it.row == row && it.col == col }?.piece
        ?: return MergeResult(grid, 0, emptySet())

    val neighbors = listOf(
        row - 1 to col,
        row + 1 to col,
        row to col - 1,
        row to col + 1
    ).mapNotNull { (r, c) ->
        grid.find { it.row == r && it.col == c }
            ?.takeIf { it.piece?.type == currentPiece.type }
    }

    if (neighbors.isEmpty()) return MergeResult(grid, 0, emptySet())

    val totalSlices = currentPiece.slices + neighbors.sumOf { it.piece!!.slices }
    val completed = totalSlices / TOTAL_PLATE_SLICES
    val remaining = totalSlices % TOTAL_PLATE_SLICES
    val points = completed * 10

    var newGrid = grid.map { cell ->
        if (neighbors.any { it.row == cell.row && it.col == cell.col })
            cell.copy(piece = null)
        else cell
    }
    newGrid = newGrid.map { cell ->
        if (cell.row == row && cell.col == col)
            cell.copy(piece = if (remaining > 0) currentPiece.copy(slices = remaining) else null)
        else cell
    }
    val disappearing = neighbors.map { it.row to it.col }.toSet()
    return if (remaining > 0) {
        val next = mergePieces(newGrid, row, col)
        MergeResult(next.grid, points + next.pointsScored, disappearingCells = disappearing + next.disappearingCells)
    } else {
        MergeResult(newGrid, points, disappearing)
    }
}
fun isGameOver(grid: List<CellState>): Boolean {
    return grid.all { it.piece != null }
}
