package com.example.gestordemaestrias.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Campus
 * Representa la tabla Campus en la base de datos
 * Estado de Registro: A=Activo, I=Inactivo, *=Eliminado
 */
@Entity(tableName = "campus")
data class Campus(
    @PrimaryKey(autoGenerate = true)
    val codigo: Int = 0,

    val nombre: String,

    // A=Activo, I=Inactivo, *=Eliminado
    val estadoRegistro: String = "A"
)