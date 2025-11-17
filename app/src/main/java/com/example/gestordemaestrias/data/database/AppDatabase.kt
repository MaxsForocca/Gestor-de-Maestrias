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
            val campusSalud = Campus(nombre = "Salud")

            val campusId1 = campusDao.insert(campusIngenierias).toInt()
            val campusId2 = campusDao.insert(campusSociales).toInt()
            val campusId3 = campusDao.insert(campusSalud).toInt()

            // Insertar Facultades
            val fips = Facultad(nombre = "FIPS")
            val educacion = Facultad(nombre = "Educación")
            val medicina = Facultad(nombre = "Medicina")
            val derecho = Facultad(nombre = "Derecho")

            val facultadId1 = facultadDao.insert(fips).toInt()
            val facultadId2 = facultadDao.insert(educacion).toInt()
            val facultadId3 = facultadDao.insert(medicina).toInt()
            val facultadId4 = facultadDao.insert(derecho).toInt()

            // Insertar Tipos de Maestría
            val investigacion = TipoMaestria(nombre = "Investigación")
            val profesional = TipoMaestria(nombre = "Profesional")

            val tipoId1 = tipoMaestriaDao.insert(investigacion).toInt()
            val tipoId2 = tipoMaestriaDao.insert(profesional).toInt()

            // Insertar Maestrías de ejemplo
            maestriaDao.insert(
                Maestria(
                    nombre = "Ingeniería de Sistemas",
                    tipoMaestriaCodigo = tipoId1,
                    facultadCodigo = facultadId1,
                    campusCodigo = campusId1
                )
            )

            maestriaDao.insert(
                Maestria(
                    nombre = "Gerencia de Sistemas",
                    tipoMaestriaCodigo = tipoId2,
                    facultadCodigo = facultadId1,
                    campusCodigo = campusId1
                )
            )

            maestriaDao.insert(
                Maestria(
                    nombre = "Salud Pública",
                    tipoMaestriaCodigo = tipoId1,
                    facultadCodigo = facultadId3,
                    campusCodigo = campusId3
                )
            )
        }
    }
}