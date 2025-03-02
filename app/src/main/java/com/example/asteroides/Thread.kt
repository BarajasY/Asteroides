package com.example.asteroides

class ThreadJuego : Thread() {
    private var pausa: Boolean = false
    private var corriendo: Boolean = false
    private val lock = Object()

    @Synchronized
    fun pausar() {
        pausa = true
    }

    @Synchronized
    fun reanudar() {
        pausa = false
        synchronized(lock) {
            lock.notify()
        }
    }

    fun detener() {
        corriendo = false
        if (pausa) reanudar()
    }

    override fun run() {
        corriendo = true
        while (corriendo) {
            synchronized(this) {
                while (pausa) {
                    try {
                        lock.wait()
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}