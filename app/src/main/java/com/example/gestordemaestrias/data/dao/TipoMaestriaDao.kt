package com.example.gestordemaestrias.data.dao

import androidx.room.*
import com.example.gestordemaestrias.data.entity.TipoMaestria
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoMaestriaDao {
    // Obtener todos los tipo_maestria (ordenados por nombre)
    @Query("SELECT * FROM tipo_maestria WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllTipoMaestria(): Flow<List<TipoMaestria>>

    // Obtener solo tipo_maestria activos (para selección en referencias)
    @Query("SELECT * FROM tipo_maestria WHERE estadoRegistro = 'A' ORDER BY nombre ASC")
    fun getActiveTipoMaestria(): Flow<List<TipoMaestria>>

    // Obtener tipo_maestria por código
    @Query("SELECT * FROM tipo_maestria WHERE codigo = :codigo")
    suspend fun getTipoMaestriaByCodigo(codigo: Int): TipoMaestria?

    // Insertar nuevo tipo_maestria
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tipoMaestria: TipoMaestria): Long

    // Actualizar tipo_maestria
    @Update
    suspend fun update(tipoMaestria: TipoMaestria)

    // Eliminar lógicamente (marca con *)
    @Query("UPDATE tipo_maestria SET estadoRegistro = '*' WHERE codigo = :codigo")
    suspend fun delete(codigo: Int)

    // Inactivar (marca con I)
    @Query("UPDATE tipo_maestria SET estadoRegistro = 'I' WHERE codigo = :codigo")
    suspend fun inactivate(codigo: Int)

    // Reactivar (marca con A)
    @Query("UPDATE tipo_maestria SET estadoRegistro = 'A' WHERE codigo = :codigo")
    suspend fun reactivate(codigo: Int)

    // Buscar por nombre
    @Query("SELECT * FROM tipo_maestria WHERE nombre LIKE '%' || :nombre || '%' AND estadoRegistro != '*' ORDER BY nombre ASC")
    fun searchByNombre(nombre: String): Flow<List<TipoMaestria>>
}