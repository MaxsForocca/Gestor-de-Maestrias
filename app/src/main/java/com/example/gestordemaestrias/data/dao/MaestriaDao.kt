package com.example.gestordemaestrias.data.dao

import androidx.room.*
import com.example.gestordemaestrias.data.entity.Maestria
import kotlinx.coroutines.flow.Flow

/**
 * Data class para la vista completa de Maestría con nombres relacionados
 */
data class MaestriaConRelaciones(
    val codigo: Int,
    val nombre: String,
    val tipoMaestriaCodigo: Int,
    val tipoMaestriaNombre: String,
    val facultadCodigo: Int,
    val facultadNombre: String,
    val campusCodigo: Int,
    val campusNombre: String,
    val estadoRegistro: String
)

@Dao
interface MaestriaDao {

    // Obtener todas las maestrías con sus relaciones
    @Query("""
        SELECT 
            m.codigo,
            m.nombre,
            m.tipoMaestriaCodigo,
            tm.nombre as tipoMaestriaNombre,
            m.facultadCodigo,
            f.nombre as facultadNombre,
            m.campusCodigo,
            c.nombre as campusNombre,
            m.estadoRegistro
        FROM maestria m
        INNER JOIN tipo_maestria tm ON m.tipoMaestriaCodigo = tm.codigo
        INNER JOIN facultad f ON m.facultadCodigo = f.codigo
        INNER JOIN campus c ON m.campusCodigo = c.codigo
        WHERE m.estadoRegistro != '*'
        ORDER BY m.nombre ASC
    """)
    fun getAllMaestriasConRelaciones(): Flow<List<MaestriaConRelaciones>>

    // Obtener maestría básica por código
    @Query("SELECT * FROM maestria WHERE codigo = :codigo")
    suspend fun getMaestriaByCodigo(codigo: Int): Maestria?

    // Insertar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(maestria: Maestria): Long

    // Actualizar
    @Update
    suspend fun update(maestria: Maestria)

    // Eliminar lógicamente
    @Query("UPDATE maestria SET estadoRegistro = '*' WHERE codigo = :codigo")
    suspend fun delete(codigo: Int)

    // Inactivar
    @Query("UPDATE maestria SET estadoRegistro = 'I' WHERE codigo = :codigo")
    suspend fun inactivate(codigo: Int)

    // Reactivar
    @Query("UPDATE maestria SET estadoRegistro = 'A' WHERE codigo = :codigo")
    suspend fun reactivate(codigo: Int)

    // Buscar por nombre
    @Query("""
        SELECT 
            m.codigo,
            m.nombre,
            m.tipoMaestriaCodigo,
            tm.nombre as tipoMaestriaNombre,
            m.facultadCodigo,
            f.nombre as facultadNombre,
            m.campusCodigo,
            c.nombre as campusNombre,
            m.estadoRegistro
        FROM maestria m
        INNER JOIN tipo_maestria tm ON m.tipoMaestriaCodigo = tm.codigo
        INNER JOIN facultad f ON m.facultadCodigo = f.codigo
        INNER JOIN campus c ON m.campusCodigo = c.codigo
        WHERE m.nombre LIKE '%' || :nombre || '%' 
        AND m.estadoRegistro != '*'
        ORDER BY m.nombre ASC
    """)
    fun searchByNombre(nombre: String): Flow<List<MaestriaConRelaciones>>
}