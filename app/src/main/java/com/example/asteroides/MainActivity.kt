package com.example.asteroides

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.setContent
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioRecorder()
//            AsteroidesTest(modifier = Modifier)
        }
    }
}

@Composable
fun MediaRecorder() {

}

@Composable
fun NativeVideoPlayer(@RawRes videoResId: Int) {
    val context = LocalContext.current
    val surfaceHolder = remember { mutableStateOf<SurfaceHolder?>(null) }
    val mediaPlayer = remember { MediaPlayer() }

    // Initialize MediaPlayer
    LaunchedEffect(Unit) {
        val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")
        mediaPlayer.setDataSource(context, videoUri)
        mediaPlayer.setOnPreparedListener { it.start() }
        mediaPlayer.setOnCompletionListener { it.seekTo(0) } // Restart on finish
        mediaPlayer.prepareAsync()
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release() // Release resources when not needed
        }
    }

    Row {
        TextButton(
            onClick = {
                mediaPlayer.pause()
            }
        ) { Text("1", fontSize = 30.sp, color = Color.White) }
        TextButton(
            onClick = {
                mediaPlayer.release()
            }
        ) { Text("2", fontSize = 30.sp, color = Color.White) }
        TextButton(
            onClick = {
                mediaPlayer.stop()
            }
        ) { Text("3", fontSize = 30.sp, color = Color.White) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            SurfaceView(ctx).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        surfaceHolder.value = holder
                        mediaPlayer.setDisplay(holder)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                    override fun surfaceDestroyed(holder: SurfaceHolder) {}
                })
            }
        }
    )
}

data class AsteroidData(val position: Offset)
data class Missile(val position: Offset, val velocity: Offset) {
    fun move(): Missile {
        return this.copy(position = position + velocity)
    }
}

fun calculateVelocity(start: Offset, target: Offset, speed: Float): Offset {
    val direction = Offset(target.x - start.x, target.y - start.y)
    val magnitude = sqrt(direction.x.pow(2) + direction.y.pow(2)) // Calculate distance
    val normalizedDirection = Offset(direction.x / magnitude, direction.y / magnitude) // Normalize
    return normalizedDirection * speed // Scale by speed
}

@Composable
fun AsteroidesTest(modifier: Modifier) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isPortrait = remember(configuration) {
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    val mediaPlayer = remember { mutableStateOf(MediaPlayer.create(context, R.raw.full)) }
    val shootingSound = remember { mutableStateOf(MediaPlayer.create(context, R.raw.shooting)) }
    val mediaPosition = remember { mutableStateOf(mediaPlayer.value.currentPosition) }

    val rotation = remember { mutableFloatStateOf(0f) }
    var position = remember { mutableStateOf(Offset(541f, 1159f)) };
    var lastTouchedPosition = remember { mutableStateOf<Offset?>(null) };

    var missileList = remember { mutableStateOf<List<Missile>>(emptyList()) }

    LaunchedEffect(Unit) {
        while (true) {
            missileList.value = missileList.value.map { it.move() }
            rotation.value += 1f
            delay(16L)
        }
    }

    LaunchedEffect(isPortrait) {
        mediaPlayer.value.seekTo(mediaPosition.value)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    mediaPlayer.value.start()
                }
                Lifecycle.Event.ON_STOP -> {
                    mediaPlayer.value.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    mediaPlayer.value.stop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val asteroidData = listOf(
        AsteroidData(Offset(200f, 200f)),
            AsteroidData(Offset(800f, 600f)),
            AsteroidData(Offset(400f, 800f)),
            AsteroidData(Offset(800f, 1500f)),
            AsteroidData(Offset(300f, 2000f))
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable()
            .onKeyEvent { key ->
                if (key.type == KeyEventType.KeyDown) {
                    when (key.key) {
                        Key.DirectionUp -> {
                            position.value = Offset(x = position.value.x, y = position.value.y - 1f)
                            true
                        }

                        Key.DirectionDown -> {
                            position.value = Offset(x = position.value.x, y = position.value.y + 1f)
                            true
                        }

                        Key.DirectionLeft -> {
                            position.value = Offset(x = position.value.x - 1f, y = position.value.y)
                            true
                        }

                        Key.DirectionRight -> {
                            position.value = Offset(x = position.value.x + 1f, y = position.value.y)
                            true
                        }
                    }
                }
                true
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val p = event.changes.first().position
                        lastTouchedPosition.value = p;
                        val missile = Missile(
                            position = position.value,
                            velocity = calculateVelocity(position.value, p, speed = 10f)
                        )
                        shootingSound.value.start();
                        missileList.value += missile
                    }
                }
            }
    ) {
        for (asteroid in asteroidData) {
            val p = Path().apply {
                val lados = 6
                for (i in 0 until lados) {
                    val angle = 2f * PI.toFloat() * i / lados
                    val x = asteroid.position.x + 100f * cos(angle)
                    val y = asteroid.position.y + 100f * sin(angle)
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }

            withTransform({
                rotate(rotation.floatValue, pivot = Offset(asteroid.position.x, asteroid.position.y))
            }) {
                drawPath(
                    path = p,
                    color = Color.Gray,
                    style = Stroke(width = 2f)
                )
            }
        }

        missileList.value.forEach { missile ->
            drawCircle(color = Color.Red, center = missile.position, radius = 10f)
        }

        val triangleWidth = 100f
        val triangleHeight = 150f

        val right = Offset(position.value.x + triangleHeight / 2, position.value.y)
        val topLeft = Offset(position.value.x - triangleHeight / 2, position.value.y - triangleWidth / 2)
        val bottomLeft = Offset(position.value.x - triangleHeight / 2, position.value.y + triangleWidth / 2)

        val path = Path().apply {
            moveTo(right.x, right.y)
            lineTo(topLeft.x, topLeft.y)
            lineTo(bottomLeft.x, bottomLeft.y)
            close()
        }

        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(width = 4f)
        )
    }
}