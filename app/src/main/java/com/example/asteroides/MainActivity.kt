package com.example.asteroides

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var vistaJuego: VistaJuego

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(vistaJuego)
//        setContent {
//            TopAppBar(
//                title = { Text("Asteroides", fontWeight = FontWeight.Bold)},
//                actions = {
//                    TopBarActions()
//                }
//            )
//            Asteroides(modifier = Modifier.fillMaxSize())
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Asteroides(modifier: Modifier, viewModel: AsteroidesViewModel = viewModel()) {

    val saldo = viewModel.getSaldo();

    val showDialog = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Asteroides",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier.width(150.dp),
            onClick = {}
        ){
            Text("Jugar", fontSize = 20.sp)
        }
        Button(
            modifier = Modifier.width(150.dp),
            onClick = {
                showDialog.value = true
            }
        ){
            Text("Acerca de", fontSize = 20.sp)
        }
        Button(
            modifier = Modifier.width(150.dp),
            onClick = {}
        ){
            Text("Settings", fontSize = 20.sp)
        }
        Button(
            modifier = Modifier.width(150.dp),
            onClick = {}
        ){
            Text("Salir", fontSize = 20.sp)
        }

        if (showDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {
                    showDialog.value = false;
                },
            ){
                Column(
                    modifier = Modifier
                        .background(
                            color = Color(0xffffffff),
                        )
                        .width(300.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Este es un texo de prueba")
                }
            }
        }
    }
}

@Composable
fun TopBarActions() {
    val menuOpen = remember { mutableStateOf(false) }

    IconButton(onClick = {}) {
        Icon(Icons.Default.Info, contentDescription = "Back")
    }
    IconButton(onClick = {}) {
        Icon(Icons.Default.Search, contentDescription = "Back")
    }
    IconButton(onClick = {
        menuOpen.value = true
    }) {
        Icon(Icons.Default.Menu, contentDescription = "Back")
    }
    DropdownMenu(
        expanded = menuOpen.value,
        onDismissRequest = {
            menuOpen.value = false
        }
    ) {
        DropdownMenuItem(
            text = {
                Text("Acerca de")
            },
            onClick = {}
        )
        DropdownMenuItem(
            text = {
                Text("Preferencias")
            },
            onClick = {}
        )
    }
}

class VistaJuego(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // /// ASTEROIDES /////
    private val asteroides: MutableList<Grafico> = ArrayList() // Lista con los Asteroides
    private val numAsteroides = 5 // Número inicial de asteroides
    private val numFragmentos = 3 // Fragmentos en que se divide

    init {
        val drawableAsteroide = AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)

        for (i in 0 until numAsteroides) {
            val asteroide = Grafico(this, drawableAsteroide!!)
            asteroide.incY = Math.random() * 4 - 2
            asteroide.incX = Math.random() * 4 - 2
            asteroide.angulo = (Math.random() * 360)
            asteroide.rotacion = (Math.random() * 8 - 4)
            asteroides.add(asteroide)
        }
    }

    override fun onSizeChanged(ancho: Int, alto: Int, anchoAnterior: Int, altoAnterior: Int) {
        super.onSizeChanged(ancho, alto, anchoAnterior, altoAnterior)
        // Una vez que conocemos nuestro ancho y alto.
        for (asteroide in asteroides) {
            asteroide.cenX = (Math.random() * ancho).toInt()
            asteroide.cenY = (Math.random() * alto).toInt()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (asteroide in asteroides) {
            asteroide.dibujaGrafico(canvas)
        }
    }
}