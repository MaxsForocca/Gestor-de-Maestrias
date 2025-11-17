package com.example.gestordemaestrias.data.repository

import com.example.gestordemaestrias.data.dao.FacultadDao
import com.example.gestordemaestrias.data.entity.Facultad
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Facultad
 * Abstrae el acceso a datos para el ViewModel
 */
class FacultadRepository(private val facultadDao: FacultadDao) {
    // Obtener todos los facultad
    fun getAllFacultad(): Flow<List<Facultad>> = facultadDao.getAllFacultad()

    // Obtener solo facultad activos (para selección)
    fun getActiveFacultad(): Flow<List<Facultad>> = facultadDao.getActiveFacultad()

    // Obtener facultad por código
    suspend fun getFacultadByCodigo(codigo: Int): Facultad? {
        return facultadDao.getFacultadByCodigo(codigo)
    }

    // Insertar nuevo facultad
    suspend fun insert(facultad: Facultad): Long {
        return facultadDao.insert(facultad)
    }

    // Actualizar facultad existente
    suspend fun update(facultad: Facultad) {
        facultadDao.update(facultad)
    }

    // Eliminar lógicamente
    suspend fun delete(codigo: Int) {
        facultadDao.delete(codigo)
    }

    // Inactivar
    suspend fun inactivate(codigo: Int) {
        facultadDao.inactivate(codigo)
    }

    // Reactivar
    suspend fun reactivate(codigo: Int) {
        facultadDao.reactivate(codigo)
    }

    // Buscar por nombre
    fun searchByNombre(nombre: String): Flow<List<Facultad>> {
        return facultadDao.searchByNombre(nombre)
    }
}