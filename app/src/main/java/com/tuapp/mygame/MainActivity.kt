package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.ui.theme.MyGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                Game()
            }
        }
    }
}
data class CellState(
    val row: Int,
    val col: Int,
    var isEmpty: Boolean = true,
    var isSelected: Boolean = false
    )

@Composable
fun Game() {
    var rows by rememberSaveable { mutableStateOf(5) }
    var cols by rememberSaveable { mutableStateOf(5) }

    val cells by remember(rows, cols) {
        mutableStateOf(
            List(rows * cols) { i ->
                CellState(row = i / cols, col = i % cols)
            }
        )
    }
    var cellStates by remember(cells) { mutableStateOf(cells) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GridSize(
            rows = rows, cols = cols,
            onRowsChange = { rows = it },
            onColsChange = { cols = it }
        )

        Spacer(Modifier.height(16.dp))

        GameGrid(
            cellStates = cellStates,
            cols = cols,
            onCellClick = { index ->
                cellStates =cellStates.toMutableList().also {
                    it[index] = it[index].copy(isSelected = !it[index].isSelected)
                }
            }
        )
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

@Composable
fun GameGrid(
    cellStates: List<CellState>,
    cols: Int,
    onCellClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(cols),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(cellStates) { index, cell ->
            GridCell(
                cell = cell,
                onClick = { onCellClick(index) }
            )
        }
    }
}

@Composable
fun GridCell(cell: CellState, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (cell.isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {}
}