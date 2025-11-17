package com.example.gestordemaestrias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.gestordemaestrias.data.database.AppDatabase
import com.example.gestordemaestrias.data.repository.*
import com.example.gestordemaestrias.ui.navigation.AppNavigation
import com.example.gestordemaestrias.ui.theme.GestorDeMaestriasTheme
import com.example.gestordemaestrias.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private lateinit var campusViewModel: CampusViewModel
    private lateinit var facultadViewModel: FacultadViewModel
    private lateinit var tipoMaestriaViewModel: TipoMaestriaViewModel
    private lateinit var maestriaViewModel: MaestriaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar base de datos
        val database = AppDatabase.getDatabase(applicationContext)

        // Crear repositorios
        val campusRepository = CampusRepository(database.campusDao())
        val facultadRepository = FacultadRepository(database.facultadDao())
        val tipoMaestriaRepository = TipoMaestriaRepository(database.tipoMaestriaDao())
        val maestriaRepository = MaestriaRepository(database.maestriaDao())

        // Crear ViewModels usando Factory
        val campusFactory = CampusViewModelFactory(campusRepository)
        campusViewModel = ViewModelProvider(this, campusFactory)[CampusViewModel::class.java]

        val facultadFactory = FacultadViewModelFactory(facultadRepository)
        facultadViewModel = ViewModelProvider(this, facultadFactory)[FacultadViewModel::class.java]

        val tipoMaestriaFactory = TipoMaestriaViewModelFactory(tipoMaestriaRepository)
        tipoMaestriaViewModel = ViewModelProvider(this, tipoMaestriaFactory)[TipoMaestriaViewModel::class.java]

        val maestriaFactory = MaestriaViewModelFactory(maestriaRepository)
        maestriaViewModel = ViewModelProvider(this, maestriaFactory)[MaestriaViewModel::class.java]

        setContent {
            GestorDeMaestriasTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        campusViewModel = campusViewModel,
                        facultadViewModel = facultadViewModel,
                        tipoMaestriaViewModel = tipoMaestriaViewModel,
                        maestriaViewModel = maestriaViewModel
                    )
                }
            }
        }
    }
}