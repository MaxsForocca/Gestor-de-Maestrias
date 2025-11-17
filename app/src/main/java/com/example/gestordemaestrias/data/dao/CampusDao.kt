package com.example.gestordemaestrias.data.dao

import androidx.room.*
import com.example.gestordemaestrias.data.entity.Campus
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // Obtener todos los campus (ordenados por nombre)
    @Query("SELECT * FROM campus WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllCampus(): Flow<List<Campus>>

    // Obtener solo campus activos (para selección en referencias)
    @Query("SELECT * FROM campus WHERE estadoRegistro = 'A' ORDER BY nombre ASC")
    fun getActiveCampus(): Flow<List<Campus>>

    // Obtener campus por código
    @Query("SELECT * FROM campus WHERE codigo = :codigo")
    suspend fun getCampusByCodigo(codigo: Int): Campus?

    // Insertar nuevo campus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campus: Campus): Long

    // Actualizar campus
    @Update
    suspend fun update(campus: Campus)

    // Eliminar lógicamente (marca con *)
    @Query("UPDATE campus SET estadoRegistro = '*' WHERE codigo = :codigo")
    suspend fun delete(codigo: Int)

    // Inactivar (marca con I)
    @Query("UPDATE campus SET estadoRegistro = 'I' WHERE codigo = :codigo")
    suspend fun inactivate(codigo: Int)

    // Reactivar (marca con A)
    @Query("UPDATE campus SET estadoRegistro = 'A' WHERE codigo = :codigo")
    suspend fun reactivate(codigo: Int)

    // Buscar por nombre
    @Query("SELECT * FROM campus WHERE nombre LIKE '%' || :nombre || '%' AND estadoRegistro != '*' ORDER BY nombre ASC")
    fun searchByNombre(nombre: String): Flow<List<Campus>>
}