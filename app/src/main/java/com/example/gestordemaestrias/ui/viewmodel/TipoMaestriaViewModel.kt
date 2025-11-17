package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordemaestrias.data.entity.TipoMaestria
import com.example.gestordemaestrias.data.repository.TipoMaestriaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class TipoMaestriaViewModel(private val repository: TipoMaestriaRepository) : ViewModel() {

    val allTipoMaestria: StateFlow<List<TipoMaestria>> = repository.getAllTipoMaestria()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTipoMaestria: StateFlow<List<TipoMaestria>> = repository.getActiveTipoMaestria()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<TipoMaestria>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllTipoMaestria()
            else repository.searchByNombre(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    fun insertTipoMaestria(nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isBlank()) {
                    _message.value = "El nombre no puede estar vacío"
                    return@launch
                }
                repository.insert(TipoMaestria(nombre = nombre.trim()))
                _message.value = "Tipo agregado exitosamente"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun updateTipoMaestria(codigo: Int, nombre: String) {
        viewModelScope.launch {
            try {
                val tipo = repository.getTipoMaestriaByCodigo(codigo)
                if (tipo != null) {
                    repository.update(tipo.copy(nombre = nombre.trim()))
                    _message.value = "Tipo actualizado"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteTipoMaestria(codigo: Int) {
        viewModelScope.launch {
            try {
                repository.delete(codigo)
                _message.value = "Tipo eliminado"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    suspend fun getTipoMaestriaByCodigo(codigo: Int) = repository.getTipoMaestriaByCodigo(codigo)

    fun clearMessage() { _message.value = null }
}