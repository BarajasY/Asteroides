package com.example.asteroides

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AsteroidesViewModel : ViewModel() {
    private val _saldo = mutableStateOf<Int>(0)

    val saldo: State<Int> get() = _saldo

    fun getSaldo(): Int {
        return saldo.value
    }
}