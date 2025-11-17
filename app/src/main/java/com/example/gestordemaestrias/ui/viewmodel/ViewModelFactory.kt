package com.example.gestordemaestrias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestordemaestrias.data.repository.*

/**
 * Factory para crear ViewModels con dependencias
 */
class CampusViewModelFactory(
    private val repository: CampusRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampusViewModel::class.java)) {
            return CampusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FacultadViewModelFactory(
    private val repository: FacultadRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FacultadViewModel::class.java)) {
            return FacultadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TipoMaestriaViewModelFactory(
    private val repository: TipoMaestriaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TipoMaestriaViewModel::class.java)) {
            return TipoMaestriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MaestriaViewModelFactory(
    private val repository: MaestriaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaestriaViewModel::class.java)) {
            return MaestriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
