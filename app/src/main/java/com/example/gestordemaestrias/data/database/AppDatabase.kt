package com.example.gestordemaestrias.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestordemaestrias.data.dao.*
import com.example.gestordemaestrias.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal de la aplicación
 * Version 1: Esquema inicial con 4 tablas
 */
@Database(
    entities = [
        Campus::class,
        Facultad::class,
        TipoMaestria::class,
        Maestria::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun campusDao(): CampusDao
    abstract fun facultadDao(): FacultadDao
    abstract fun tipoMaestriaDao(): TipoMaestriaDao
    abstract fun maestriaDao(): MaestriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestor_maestrias_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback para poblar la base de datos con datos iniciales
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        /**
         * Inserta datos de ejemplo al crear la base de datos
         */
        private suspend fun populateDatabase(database: AppDatabase) {
            val campusDao = database.campusDao()
            val facultadDao = database.facultadDao()
            val tipoMaestriaDao = database.tipoMaestriaDao()
            val maestriaDao = database.maestriaDao()

            // Insertar Campus
            val campusIngenierias = Campus(nombre = "Ingenierías")
            val campusSociales = Campus(nombre = "Sociales")

            val campusId1 = campusDao.insert(campusIngenierias).toInt()
            val campusId2 = campusDao.insert(campusSociales).toInt()

            // Insertar Facultades
            val fips = Facultad(nombre = "Facultad de Ingeniería de Producción y Servicios")
            val cienciasHistoricos = Facultad(nombre = "Facultad de Ciencias Histórico Sociales")
            val cienciasNaturales = Facultad(nombre = "Facultad de Ciencias Naturales y Formales")

            val facultadId1 = facultadDao.insert(fips).toInt()
            val facultadId2 = facultadDao.insert(cienciasHistoricos).toInt()
            val facultadId3 = facultadDao.insert(cienciasNaturales).toInt()

            // Insertar Tipos de Maestría
            val investigacion = TipoMaestria(nombre = "Maestria Profesional")
            val profesional = TipoMaestria(nombre = "Maestria Academica")

            val tipoId1 = tipoMaestriaDao.insert(investigacion).toInt()
            val tipoId2 = tipoMaestriaDao.insert(profesional).toInt()

            // Insertar Maestrías de ejemplo
            maestriaDao.insert(
                Maestria(
                    nombre = "Maestría en Ciencia de Datos",
                    tipoMaestriaCodigo = tipoId1,
                    facultadCodigo = facultadId1,
                    campusCodigo = campusId1
                )
            )

            maestriaDao.insert(
                Maestria(
                    nombre = "Maestría en Ciencias: con mención en Comunicación",
                    tipoMaestriaCodigo = tipoId2,
                    facultadCodigo = facultadId2,
                    campusCodigo = campusId2
                )
            )
        }
    }
}