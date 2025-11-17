package com.example.gestordemaestrias.data.repository

import com.example.gestordemaestrias.data.dao.CampusDao
import com.example.gestordemaestrias.data.entity.Campus
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Campus
 * Abstrae el acceso a datos para el ViewModel
 */
class CampusRepository(private val campusDao: CampusDao) {

    // Obtener todos los campus
    fun getAllCampus(): Flow<List<Campus>> = campusDao.getAllCampus()

    // Obtener solo campus activos (para selección)
    fun getActiveCampus(): Flow<List<Campus>> = campusDao.getActiveCampus()

    // Obtener campus por código
    suspend fun getCampusByCodigo(codigo: Int): Campus? {
        return campusDao.getCampusByCodigo(codigo)
    }

    // Insertar nuevo campus
    suspend fun insert(campus: Campus): Long {
        return campusDao.insert(campus)
    }

    // Actualizar campus existente
    suspend fun update(campus: Campus) {
        campusDao.update(campus)
    }

    // Eliminar lógicamente
    suspend fun delete(codigo: Int) {
        campusDao.delete(codigo)
    }

    // Inactivar
    suspend fun inactivate(codigo: Int) {
        campusDao.inactivate(codigo)
    }

    // Reactivar
    suspend fun reactivate(codigo: Int) {
        campusDao.reactivate(codigo)
    }

    // Buscar por nombre
    fun searchByNombre(nombre: String): Flow<List<Campus>> {
        return campusDao.searchByNombre(nombre)
    }
}