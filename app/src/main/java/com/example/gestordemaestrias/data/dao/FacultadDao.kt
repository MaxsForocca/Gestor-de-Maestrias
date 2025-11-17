package com.example.gestordemaestrias.data.dao

import androidx.room.*
import com.example.gestordemaestrias.data.entity.Facultad
import kotlinx.coroutines.flow.Flow

@Dao
interface FacultadDao {

    // Obtener todos los facultad (ordenados por nombre)
    @Query("SELECT * FROM facultad WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllFacultad(): Flow<List<Facultad>>

    // Obtener solo facultad activos (para selección en referencias)
    @Query("SELECT * FROM facultad WHERE estadoRegistro = 'A' ORDER BY nombre ASC")
    fun getActiveFacultad(): Flow<List<Facultad>>

    // Obtener facultad por código
    @Query("SELECT * FROM facultad WHERE codigo = :codigo")
    suspend fun getFacultadByCodigo(codigo: Int): Facultad?

    // Insertar nuevo facultad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(facultad: Facultad): Long

    // Actualizar facultad
    @Update
    suspend fun update(facultad: Facultad)

    // Eliminar lógicamente (marca con *)
    @Query("UPDATE facultad SET estadoRegistro = '*' WHERE codigo = :codigo")
    suspend fun delete(codigo: Int)

    // Inactivar (marca con I)
    @Query("UPDATE facultad SET estadoRegistro = 'I' WHERE codigo = :codigo")
    suspend fun inactivate(codigo: Int)

    // Reactivar (marca con A)
    @Query("UPDATE facultad SET estadoRegistro = 'A' WHERE codigo = :codigo")
    suspend fun reactivate(codigo: Int)

    // Buscar por nombre
    @Query("SELECT * FROM facultad WHERE nombre LIKE '%' || :nombre || '%' AND estadoRegistro != '*' ORDER BY nombre ASC")
    fun searchByNombre(nombre: String): Flow<List<Facultad>>
}