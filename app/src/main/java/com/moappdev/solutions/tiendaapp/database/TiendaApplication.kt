package com.moappdev.solutions.tiendaapp.database

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TiendaApplication:Application() {
    companion object{
        lateinit var database: TiendaDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2= object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                //database.execSQL(alter table TiendaEntity add column imagen text not null default ""
            }
        }

        database= Room.databaseBuilder(
            this,
            TiendaDatabase::class.java,
            "TiendaDatabase")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}