package com.hooked.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GaugeBar(label: String, progress: Float, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(Color.DarkGray)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(16.dp)
                    .background(color)
            )
        }
    }
}

@Composable
fun TVButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.Button(onClick = onClick, modifier = modifier.padding(6.dp)) {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
}
