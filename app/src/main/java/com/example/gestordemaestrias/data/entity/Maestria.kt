package com.example.gestordemaestrias.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad principal Maestria
 * Referencia a: TipoMaestria, Facultad, Campus
 */
@Entity(
    tableName = "maestria",
    foreignKeys = [
        ForeignKey(
            entity = TipoMaestria::class,
            parentColumns = ["codigo"],
            childColumns = ["tipoMaestriaCodigo"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Facultad::class,
            parentColumns = ["codigo"],
            childColumns = ["facultadCodigo"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Campus::class,
            parentColumns = ["codigo"],
            childColumns = ["campusCodigo"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("tipoMaestriaCodigo"),
        Index("facultadCodigo"),
        Index("campusCodigo")
    ]
)
data class Maestria(
    @PrimaryKey(autoGenerate = true)
    val codigo: Int = 0,

    val nombre: String,

    val tipoMaestriaCodigo: Int,

    val facultadCodigo: Int,

    val campusCodigo: Int,

    val estadoRegistro: String = "A"
)