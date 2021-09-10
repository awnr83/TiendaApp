package com.moappdev.solutions.tiendaapp

import com.moappdev.solutions.tiendaapp.database.Tienda

interface MainAux {
    fun hideBtnFab(visible: Boolean = false)

    fun addTienda(tienda: Tienda)
    fun updateTienda(tienda: Tienda)
}