package com.example.asteroides

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

@Composable
fun AsteroidesTest(modifier: Modifier) {

    val rotation = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            rotation.value += 1f
            delay(16L)
        }
    }

    val asteroidData = listOf(
        AsteroidData(Offset(200f, 200f)),
            AsteroidData(Offset(800f, 600f)),
            AsteroidData(Offset(400f, 800f)),
            AsteroidData(Offset(800f, 1500f)),
            AsteroidData(Offset(300f, 2000f))
    )

    Canvas(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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

        val centerX = size.width / 2
        val centerY = size.height / 2

        val triangleWidth = 100f
        val triangleHeight = 150f

        val right = Offset(centerX + triangleHeight / 2, centerY)
        val topLeft = Offset(centerX - triangleHeight / 2, centerY - triangleWidth / 2)
        val bottomLeft = Offset(centerX - triangleHeight / 2, centerY + triangleWidth / 2)

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