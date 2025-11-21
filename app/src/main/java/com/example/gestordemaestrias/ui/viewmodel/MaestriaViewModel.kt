package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordemaestrias.data.dao.MaestriaConRelaciones
import com.example.gestordemaestrias.data.entity.Facultad
import com.example.gestordemaestrias.data.entity.Maestria
import com.example.gestordemaestrias.data.repository.MaestriaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class MaestriaViewModel(private val repository: MaestriaRepository) : ViewModel() {

    val allMaestrias = repository.getAllMaestriasConRelaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<MaestriaConRelaciones>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllMaestriasConRelaciones()
            else repository.searchByNombre(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateSearchQuery(query: String) {_searchQuery.value = query}

    fun insertMaestria(nombre: String, tipoMaestriaCodigo: Int, facultadCodigo: Int, campusCodigo: Int) {
        viewModelScope.launch {
            try {
                repository.insert(Maestria(
                    nombre = nombre,
                    tipoMaestriaCodigo = tipoMaestriaCodigo,
                    facultadCodigo = facultadCodigo,
                    campusCodigo = campusCodigo
                ))
                _message.value = "Maestría agregada exitosamente"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun updateMaestria(codigo: Int, nombre: String, tipoMaestriaCodigo: Int, facultadCodigo: Int, campusCodigo: Int) {
        viewModelScope.launch {
            try {
                val maestria = repository.getMaestriaByCodigo(codigo)
                if (maestria != null) {
                    repository.update(maestria.copy(
                        nombre = nombre,
                        tipoMaestriaCodigo = tipoMaestriaCodigo,
                        facultadCodigo = facultadCodigo,
                        campusCodigo = campusCodigo
                    ))
                    _message.value = "Maestría actualizada"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteMaestria(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.delete(codigo)
                _message.value = "Maestria eliminada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun inactivateMaestria(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.inactivate(codigo)
                _message.value = "Maestria inactivada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun reactivateMaestria(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.reactivate(codigo)
                _message.value = "Maestria reactivada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    suspend fun getMaestriaByCodigo(codigo: Int) = repository.getMaestriaByCodigo(codigo)

    fun clearMessage() { _message.value = null }
}