package com.example.gestordemaestrias.data.repository

import com.example.gestordemaestrias.data.dao.TipoMaestriaDao
import com.example.gestordemaestrias.data.entity.TipoMaestria
import kotlinx.coroutines.flow.Flow

/**
 * Repository para TipoMaestria
 * Abstrae el acceso a datos para el ViewModel
 */
class TipoMaestriaRepository(private val tipoMaestriaDao: TipoMaestriaDao) {
    // Obtener todos los tipoMaestria
    fun getAllTipoMaestria(): Flow<List<TipoMaestria>> = tipoMaestriaDao.getAllTipoMaestria()

    // Obtener solo tipoMaestria activos (para selección)
    fun getActiveTipoMaestria(): Flow<List<TipoMaestria>> = tipoMaestriaDao.getActiveTipoMaestria()

    // Obtener tipoMaestria por código
    suspend fun getTipoMaestriaByCodigo(codigo: Int): TipoMaestria? {
        return tipoMaestriaDao.getTipoMaestriaByCodigo(codigo)
    }

    // Insertar nuevo tipoMaestria
    suspend fun insert(tipoMaestria: TipoMaestria): Long {
        return tipoMaestriaDao.insert(tipoMaestria)
    }

    // Actualizar tipoMaestria existente
    suspend fun update(tipoMaestria: TipoMaestria) {
        tipoMaestriaDao.update(tipoMaestria)
    }

    // Eliminar lógicamente
    suspend fun delete(codigo: Int) {
        tipoMaestriaDao.delete(codigo)
    }

    // Inactivar
    suspend fun inactivate(codigo: Int) {
        tipoMaestriaDao.inactivate(codigo)
    }

    // Reactivar
    suspend fun reactivate(codigo: Int) {
        tipoMaestriaDao.reactivate(codigo)
    }

    // Buscar por nombre
    fun searchByNombre(nombre: String): Flow<List<TipoMaestria>> {
        return tipoMaestriaDao.searchByNombre(nombre)
    }
}