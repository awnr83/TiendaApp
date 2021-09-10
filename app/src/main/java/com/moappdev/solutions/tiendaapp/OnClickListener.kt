package com.moappdev.solutions.tiendaapp

import com.moappdev.solutions.tiendaapp.database.Tienda

interface OnClickListener{
    fun onClick(tiendaId: Long)
    fun onFavorito(tienda: Tienda)
    fun onDelete(tienda: Tienda)
}