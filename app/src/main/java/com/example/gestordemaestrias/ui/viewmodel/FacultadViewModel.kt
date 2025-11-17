package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordemaestrias.data.entity.Facultad
import com.example.gestordemaestrias.data.repository.FacultadRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class FacultadViewModel(private val repository: FacultadRepository) : ViewModel() {

    val allFacultad: StateFlow<List<Facultad>> = repository.getAllFacultad()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeFacultad: StateFlow<List<Facultad>> = repository.getActiveFacultad()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Facultad>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllFacultad()
            else repository.searchByNombre(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    fun insertFacultad(nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isBlank()) {
                    _message.value = "El nombre no puede estar vacío"
                    return@launch
                }
                repository.insert(Facultad(nombre = nombre.trim()))
                _message.value = "Facultad agregada exitosamente"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun updateFacultad(codigo: Int, nombre: String) {
        viewModelScope.launch {
            try {
                val facultad = repository.getFacultadByCodigo(codigo)
                if (facultad != null) {
                    repository.update(facultad.copy(nombre = nombre.trim()))
                    _message.value = "Facultad actualizada"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteFacultad(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.delete(codigo)
                _message.value = "Facultad eliminada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun inactivateFacultad(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.inactivate(codigo)
                _message.value = "Facultad inactivada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun reactivateFacultad(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.reactivate(codigo)
                _message.value = "Facultad reactivada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    suspend fun getFacultadByCodigo(codigo: Int) = repository.getFacultadByCodigo(codigo)

    fun clearMessage() { _message.value = null }
}