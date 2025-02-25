package com.example.asteroides

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EjemploProgressDialog() {
    var showProgress by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            showProgress = true
            coroutineScope.launch {
                // Simulate a background task
                for (i in 1..10) {
                    delay(500) // Simulate work
                    progress = i / 10f // Update progress
                }
                showProgress = false // Hide progress dialog after task completes
            }
        }) {
            Text("Start Task")
        }

        if (showProgress) {
            ProgressDialog(progress = progress)
        }
    }
}

@Composable
fun ProgressDialog(progress: Float) {
    AlertDialog(
        onDismissRequest = { /* Dialog cannot be dismissed by clicking outside */ },
        title = { Text("Loading") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${(progress * 100).toInt()}%")
            }
        },
        confirmButton = {}
    )
}