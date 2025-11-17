package com.example.gestordemaestrias.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Facultad
 * Ejemplos: FIPS, Educación, Medicina
 */
@Entity(tableName = "facultad")
data class Facultad(
    @PrimaryKey(autoGenerate = true)
    val codigo: Int = 0,

    val nombre: String,

    val estadoRegistro: String = "A"
)