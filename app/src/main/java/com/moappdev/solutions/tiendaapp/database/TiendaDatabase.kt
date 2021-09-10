package com.moappdev.solutions.tiendaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Tienda::class), version = 2)
abstract class TiendaDatabase:RoomDatabase() {
    abstract fun tiendaDao(): TiendaDao
}