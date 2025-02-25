package com.example.asteroides

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.platform.LocalContext
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
            AsteroidesTest(modifier = Modifier.fillMaxSize())
        }
    }
}

data class AsteroidData(val position: Offset)
// Missile data class with movement logic
data class Missile(val position: Offset, val velocity: Offset) {
    fun move(): Missile {
        return this.copy(position = position + velocity)
    }
}

// Calculate velocity based on direction from ship to touch position
fun calculateVelocity(start: Offset, target: Offset, speed: Float): Offset {
    val direction = Offset(target.x - start.x, target.y - start.y)
    val magnitude = sqrt(direction.x.pow(2) + direction.y.pow(2)) // Calculate distance
    val normalizedDirection = Offset(direction.x / magnitude, direction.y / magnitude) // Normalize
    return normalizedDirection * speed // Scale by speed
}

@Composable
fun AsteroidesTest(modifier: Modifier) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensors = remember { sensorManager.getSensorList(Sensor.TYPE_ALL) }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }
    val rotation = remember { mutableFloatStateOf(0f) }
    var position = remember { mutableStateOf(Offset(541f, 1159f)) };
    var lastTouchedPosition = remember { mutableStateOf<Offset?>(null) };

    var missileList = remember { mutableStateOf<List<Missile>>(emptyList()) }

    var azimuth = remember { mutableStateOf(0f) } // Yaw (Rotation around Z-axis)
    var pitch = remember { mutableStateOf(0f) }   // Rotation around X-axis
    var previousPitch = remember { mutableStateOf(0f) }   // Rotation around X-axis
    var roll = remember { mutableStateOf(0f) }    // Rotation around Y-axis
    var previousRoll = remember { mutableStateOf(0f) }    // Rotation around Y-axis

    LaunchedEffect(Unit) {
        while (true) {
            missileList.value = missileList.value.map { it.move() }
            rotation.value += 1f
//            if (lastTouchedPosition.value != null) {
//                position.value = lastTouchedPosition.value!!;
//                lastTouchedPosition.value = null;
//            }
//            if (pitch.value != previousPitch.value) {
//                if (pitch.value < 0) {
//                    position.value = Offset(x = position.value.x, y = position.value.y - 1f)
//                }
//                if (pitch.value > 0) {
//                    position.value = Offset(x = position.value.x, y = position.value.y + 1f)
//                }
//            }
//
//            if (roll.value != previousRoll.value) {
//                if (roll.value < 0) {
//                    position.value = Offset(x = position.value.x - 1f, y = position.value.y)
//                }
//
//                if (roll.value > 0) {
//                    position.value = Offset(x = position.value.x + 1f, y = position.value.y)
//                }
//            }
//            previousRoll.value = roll.value
//            previousPitch.value = pitch.value
//
//            position.value = position.value.copy(
//                x = position.value.x + 1f,
//                y = position.value.y + 1f
//            )
            delay(16L)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { values ->
                    val rotationMatrix = FloatArray(9)
                    val orientationValues = FloatArray(3)

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)

                    azimuth.value = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    pitch.value = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
                    roll.value = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val asteroidData = listOf(
        AsteroidData(Offset(200f, 200f)),
            AsteroidData(Offset(800f, 600f)),
            AsteroidData(Offset(400f, 800f)),
            AsteroidData(Offset(800f, 1500f)),
            AsteroidData(Offset(300f, 2000f))
    )
    // EjemploProgressDialog()
    // CodeView()

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
                        missileList.value += missile

                        // val touchPoints = event.changes.map { it.position }
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

fun factorial(valor: Int): Int {
    var res = 1;
    for (loop in 1..valor) {
        res *= loop
        SystemClock.sleep(1000)
    }
    return res
}

fun bloquearHiloPrincipal() {
    val n = 1000;
    val factorial = factorial(n)
}

fun crearHilo() {
    val hilo = Thread {
        println("Esto fue creado en otro hilo")
    }
    hilo.start();
}

class MyAsyncTask : AsyncTask<Unit, Int, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        println("AsyncTask started")
    }

    override fun doInBackground(vararg params: Unit?): String {
        for (i in 1..10) {
            SystemClock.sleep(500)
            publishProgress(i * 10)
        }
        return "Task completed"
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        println("Progress: ${values[0]}%")
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        println(result)
    }

    override fun onCancelled(result: String?) {
        super.onCancelled(result)
    }
}

class SharedResource {
    private val lock = Any() // Use any object as a lock

    fun doSomething() {
        synchronized(lock) {
            // Only one thread can execute this block at a time
            println("Thread-safe operation")
        }
    }
}