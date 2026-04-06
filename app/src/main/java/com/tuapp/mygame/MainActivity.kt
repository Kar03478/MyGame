package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.ui.theme.MyGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}
// difColor(coord x: Int, coord y: Int, colour: Int)
data class CellState(
    val row: Int,
    val col: Int,
    var isEmpty: Boolean = true
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

