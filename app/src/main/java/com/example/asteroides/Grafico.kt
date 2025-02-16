package com.example.asteroides

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import kotlin.math.hypot

class Grafico(private val view: View, private val drawable: Drawable) {
    var cenX: Int = 0
    var cenY: Int = 0
    val ancho: Int = drawable.intrinsicWidth
    val alto: Int = drawable.intrinsicHeight
    var incX: Double = 0.0
    var incY: Double = 0.0
    var angulo: Double = 0.0
    var rotacion: Double = 0.0
    val radioColision: Int = (alto + ancho) / 4
    val radioInval: Int = hypot(ancho / 2.0, alto / 2.0).toInt()
    var xAnterior: Int = 0
    var yAnterior: Int = 0

    fun dibujaGrafico(canvas: Canvas) {
        val x = cenX - ancho / 2
        val y = cenY - alto / 2
        drawable.setBounds(x, y, x + ancho, y + alto)
        canvas.save()
        canvas.rotate(angulo.toFloat(), cenX.toFloat(), cenY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
        xAnterior = cenX
        yAnterior = cenY
    }

    fun incrementaPos(factor: Double) {
        cenX += (incX * factor).toInt()
        cenY += (incY * factor).toInt()
        angulo += rotacion * factor

        // Correct position if we go out of the screen
        if (cenX < 0) cenX = view.width
        if (cenX > view.width) cenX = 0
        if (cenY < 0) cenY = view.height
        if (cenY > view.height) cenY = 0

        view.postInvalidate(cenX - radioInval, cenY - radioInval, cenX + radioInval, cenY + radioInval)
        view.postInvalidate(xAnterior - radioInval, yAnterior - radioInval, xAnterior + radioInval, yAnterior + radioInval)
    }

    fun distancia(g: Grafico): Double {
        return hypot((cenX - g.cenX).toDouble(), (cenY - g.cenY).toDouble())
    }

    fun verificaColision(g: Grafico): Boolean {
        return distancia(g) < (radioColision + g.radioColision)
    }
}