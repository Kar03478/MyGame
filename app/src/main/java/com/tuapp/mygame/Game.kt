package com.tuapp.mygame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun Game(vm: GameViewModel = viewModel()) {
    val cells by vm.cells.collectAsStateWithLifecycle()
    val tray by vm.tray.collectAsStateWithLifecycle()
    val rows by vm.rows.collectAsStateWithLifecycle()
    val cols by vm.cols.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        GridSize(
            rows = rows, cols = cols,
            onRowsChange = { vm.resizeGrid(it, cols) },
            onColsChange = { vm.resizeGrid(rows, it) }
        )

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(cols),
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(cells) { _, cell ->
                GridCell(
                    cell = cell,
                    onDrop = { trayIndex ->
                        vm.placePiece(trayIndex, cell.row, cell.col)
                    }
                )
            }
        }
        Spacer(Modifier.height(32.dp))

        GameTray(pieces = tray)
    }
}

@Composable
fun GridSize(
    rows: Int, cols: Int,
    onRowsChange: (Int) -> Unit,
    onColsChange: (Int) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.Rows) + rows)
        Slider(
            value = rows.toFloat(),
            onValueChange = { onRowsChange(it.toInt())},
            valueRange = 2f..10f,
            steps = 7
        )
        Text(text = stringResource(R.string.Cols) + cols)
        Slider(
            value = cols.toFloat(),
            onValueChange = { onColsChange(it.toInt())},
            valueRange = 2f..10f,
            steps = 7
        )
    }
}
