package com.moappdev.solutions.tiendaapp.database

import androidx.room.*
import com.moappdev.solutions.tiendaapp.database.Tienda

@Dao
interface TiendaDao {

    @Query("select * from TiendaEntity order by favorita desc")
    fun allTiendas(): MutableList<Tienda>

    @Query("select * from TiendaEntity where id= :tiendaId")
    fun getTienda(tiendaId:Long): Tienda

    @Insert
    fun addTienda(tienda: Tienda): Long

    @Delete
    fun deleteTiienda(tienda: Tienda)

    @Update
    fun updateTienda(tienda: Tienda)
}