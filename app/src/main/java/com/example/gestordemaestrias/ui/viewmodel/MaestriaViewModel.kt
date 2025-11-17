package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordemaestrias.data.entity.Maestria
import com.example.gestordemaestrias.data.repository.MaestriaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class MaestriaViewModel(private val repository: MaestriaRepository) : ViewModel() {

    val allMaestrias = repository.getAllMaestriasConRelaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

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

    suspend fun getMaestriaByCodigo(codigo: Int) = repository.getMaestriaByCodigo(codigo)

    fun clearMessage() { _message.value = null }
}