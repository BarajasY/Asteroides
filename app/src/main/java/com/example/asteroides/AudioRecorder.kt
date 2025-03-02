package com.example.asteroides

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.IOException

@Composable
fun AudioRecorder() {
    val context = LocalContext.current
    val recorder = remember { MediaRecorder() }
    val player = remember { MediaPlayer() }

    var isRecording = remember { mutableStateOf(false) }
    var isPlaying = remember { mutableStateOf(false) }
    var audioFilePath = remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
            )
        )
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (!isRecording.value) {
                    audioFilePath.value = "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/audio_record.3gp"
                    startRecording(recorder, audioFilePath.value)
                    isRecording.value = true
                } else {
                    stopRecording(recorder)
                    isRecording.value = false
                }
            }
        ) {
            Text(if (isRecording.value) "Detener Grabación" else "Iniciar Grabación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isPlaying.value) {
                    playAudio(player, audioFilePath.value)
                    isPlaying.value = true
                } else {
                    player.stop()
                    player.reset()
                    isPlaying.value = false
                }
            },
            enabled = audioFilePath.value.isNotEmpty()
        ) {
            Text(if (isPlaying.value) "Detener Reproducción" else "Reproducir Audio")
        }
    }
}

// Función para iniciar la grabación
fun startRecording(recorder: MediaRecorder, filePath: String) {
    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        setOutputFile(filePath)

        try {
            prepare()
            start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

// Función para detener la grabación
fun stopRecording(recorder: MediaRecorder) {
    try {
        recorder.apply {
            stop()
            release()
        }
    } catch (e: RuntimeException) {
        e.printStackTrace()
    }
}

// Función para reproducir el audio grabado
fun playAudio(player: MediaPlayer, filePath: String) {
    player.apply {
        setDataSource(filePath)
        prepare()
        start()
    }
}
