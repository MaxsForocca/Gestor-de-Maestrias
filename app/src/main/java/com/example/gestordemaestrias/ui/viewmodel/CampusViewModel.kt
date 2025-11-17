package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordemaestrias.data.entity.Campus
import com.example.gestordemaestrias.data.repository.CampusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar Campus
 * Maneja el estado de la UI y la lógica de negocio
 */
class CampusViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    // Estado para la lista de campus
    val allCampus: StateFlow<List<Campus>> = repository.getAllCampus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para campus activos (para selector)
    val activeCampus: StateFlow<List<Campus>> = repository.getActiveCampus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Resultados de búsqueda
    val searchResults: StateFlow<List<Campus>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllCampus()
            } else {
                repository.searchByNombre(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para mensajes/errores
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // Actualizar query de búsqueda
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Insertar nuevo campus
    fun insertCampus(nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isBlank()) {
                    _message.value = "El nombre no puede estar vacío"
                    return@launch
                }

                val campus = Campus(nombre = nombre.trim())
                repository.insert(campus)
                _message.value = "Campus agregado exitosamente"
            } catch (e: Exception) {
                _message.value = "Error al agregar campus: ${e.message}"
            }
        }
    }

    // Actualizar campus existente
    fun updateCampus(codigo: Int, nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isBlank()) {
                    _message.value = "El nombre no puede estar vacío"
                    return@launch
                }

                val campus = repository.getCampusByCodigo(codigo)
                if (campus != null) {
                    val updatedCampus = campus.copy(nombre = nombre.trim())
                    repository.update(updatedCampus)
                    _message.value = "Campus actualizado exitosamente"
                } else {
                    _message.value = "Campus no encontrado"
                }
            } catch (e: Exception) {
                _message.value = "Error al actualizar campus: ${e.message}"
            }
        }
    }

    // Eliminar campus
    fun deleteCampus(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.delete(codigo)
                _message.value = "Campus eliminado"
            } catch (e: Exception) {
                _message.value = "Error al eliminar campus: ${e.message}"
            }
        }
    }

    // Inactivar campus
    fun inactivateCampus(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.inactivate(codigo)
                _message.value = "Campus inactivado"
            } catch (e: Exception) {
                _message.value = "Error al inactivar campus: ${e.message}"
            }
        }
    }

    // Reactivar campus
    fun reactivateCampus(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.reactivate(codigo)
                _message.value = "Campus reactivado"
            } catch (e: Exception) {
                _message.value = "Error al reactivar campus: ${e.message}"
            }
        }
    }

    // Obtener campus por código
    suspend fun getCampusByCodigo(codigo: Int): Campus? {
        return repository.getCampusByCodigo(codigo)
    }

    // Limpiar mensaje
    fun clearMessage() {
        _message.value = null
    }
}