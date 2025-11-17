package com.example.gestordemaestrias.data.repository

import com.example.gestordemaestrias.data.dao.MaestriaDao
import com.example.gestordemaestrias.data.entity.Maestria
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Campus
 * Abstrae el acceso a datos para el ViewModel
 */
class MaestriaRepository(private val maestriaDao: MaestriaDao) {
    fun getAllMaestriasConRelaciones() = maestriaDao.getAllMaestriasConRelaciones()

    suspend fun getMaestriaByCodigo(codigo: Int): Maestria? {
        return maestriaDao.getMaestriaByCodigo(codigo)
    }

    suspend fun insert(maestria: Maestria): Long {
        return maestriaDao.insert(maestria)
    }

    suspend fun update(maestria: Maestria) {
        maestriaDao.update(maestria)
    }

    suspend fun delete(codigo: Int) {
        maestriaDao.delete(codigo)
    }

    suspend fun inactivate(codigo: Int) {
        maestriaDao.inactivate(codigo)
    }

    suspend fun reactivate(codigo: Int) {
        maestriaDao.reactivate(codigo)
    }

    fun searchByNombre(nombre: String) = maestriaDao.searchByNombre(nombre)
}