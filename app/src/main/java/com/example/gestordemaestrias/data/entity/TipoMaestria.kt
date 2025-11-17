package com.example.gestordemaestrias.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad TipoMaestria
 * Ejemplos: Investigación, Profesional
 */
@Entity(tableName = "tipo_maestria")
data class TipoMaestria(
    @PrimaryKey(autoGenerate = true)
    val codigo: Int = 0,

    val nombre: String,

    val estadoRegistro: String = "A"
)