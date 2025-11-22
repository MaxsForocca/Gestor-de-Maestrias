package com.example.gestordemaestrias.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestordemaestrias.ui.screens.MenuPrincipalScreen
import com.example.gestordemaestrias.ui.screens.campus.*
import com.example.gestordemaestrias.ui.screens.facultad.*
import com.example.gestordemaestrias.ui.screens.tipomaestria.*
import com.example.gestordemaestrias.ui.screens.maestria.*
import com.example.gestordemaestrias.ui.viewmodel.*

/**
 * Rutas de navegación de la aplicación
 */
sealed class Screen(val route: String) {
    object MenuPrincipal : Screen("menu_principal")

    // Campus
    object CampusList : Screen("campus_list")
    object CampusForm : Screen("campus_form/{codigo}") {
        fun createRoute(codigo: Int?) = if (codigo == null)
            "campus_form/null"
        else
            "campus_form/$codigo"
    }

    // Facultad
    object FacultadList : Screen("facultad_list")
    object FacultadForm : Screen("facultad_form/{codigo}") {
        fun createRoute(codigo: Int?) = if (codigo == null)
            "facultad_form/null"
        else
            "facultad_form/$codigo"
    }

    // Tipo Maestría
    object TipoMaestriaList : Screen("tipo_maestria_list")
    object TipoMaestriaForm : Screen("tipo_maestria_form/{codigo}") {
        fun createRoute(codigo: Int?) = if (codigo == null)
            "tipo_maestria_form/null"
        else
            "tipo_maestria_form/$codigo"
    }

    // Maestría
    object MaestriaList : Screen("maestria_list")
    object MaestriaForm : Screen("maestria_form/{codigo}") {
        fun createRoute(codigo: Int?) = if (codigo == null)
            "maestria_form/null"
        else
            "maestria_form/$codigo"
    }
}

@Composable
fun AppNavigation(
    campusViewModel: CampusViewModel,
    facultadViewModel: FacultadViewModel,
    tipoMaestriaViewModel: TipoMaestriaViewModel,
    maestriaViewModel: MaestriaViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MenuPrincipal.route
    ) {
        // Menú Principal
        composable(Screen.MenuPrincipal.route) {
            MenuPrincipalScreen(
                onNavigateToCampus = { navController.navigate(Screen.CampusList.route) },
                onNavigateToFacultad = { navController.navigate(Screen.FacultadList.route) },
                onNavigateToTipoMaestria = { navController.navigate(Screen.TipoMaestriaList.route) },
                onNavigateToMaestria = { navController.navigate(Screen.MaestriaList.route) }
            )
        }

        // Campus - Lista
        composable(Screen.CampusList.route) {
            CampusListScreen(
                viewModel = campusViewModel,
                onNavigateToForm = { codigo ->
                    navController.navigate(Screen.CampusForm.createRoute(codigo))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Campus - Formulario
        composable(
            route = Screen.CampusForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val codigoString = backStackEntry.arguments?.getString("codigo")
            val codigo = if (codigoString == "null") null else codigoString?.toIntOrNull()

            CampusFormScreen(
                viewModel = campusViewModel,
                campusCodigo = codigo,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // TODO: Agregar las rutas similares para Facultad, TipoMaestria y Maestria
        // siguiendo el mismo patrón
        // Facultad - Lista
        composable(Screen.FacultadList.route) {
            FacultadListScreen(
                viewModel = facultadViewModel,
                onNavigateToForm = { codigo ->
                    navController.navigate(Screen.FacultadForm.createRoute(codigo))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Facultad - Formulario
        composable(
            route = Screen.FacultadForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val codigoString = backStackEntry.arguments?.getString("codigo")
            val codigo = if (codigoString == "null") null else codigoString?.toIntOrNull()

            FacultadFormScreen(
                viewModel = facultadViewModel,
                facultadCodigo = codigo,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Tipo Maestria - Lista
        composable(Screen.TipoMaestriaList.route) {
            TipoMaestriaListScreen(
                viewModel = tipoMaestriaViewModel,
                onNavigateToForm = { codigo ->
                    navController.navigate(Screen.TipoMaestriaForm.createRoute(codigo))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Tipo Maestria - Formulario
        composable(
            route = Screen.TipoMaestriaForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val codigoString = backStackEntry.arguments?.getString("codigo")
            val codigo = if (codigoString == "null") null else codigoString?.toIntOrNull()

            TipoMaestriaFormScreen(
                viewModel = tipoMaestriaViewModel,
                tipoMaestriaCodigo = codigo,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Maestria - Lista
        composable(Screen.MaestriaList.route) {
            MaestriaListScreen(
                viewModel = maestriaViewModel,
                campusViewModel = campusViewModel,
                facultadViewModel = facultadViewModel,
                tipoMaestriaViewModel = tipoMaestriaViewModel,
                onNavigateToForm = { codigo ->
                    navController.navigate(Screen.MaestriaForm.createRoute(codigo))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Maestria - Formulario
        composable(
            route = Screen.MaestriaForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val codigoString = backStackEntry.arguments?.getString("codigo")
            val codigo = if (codigoString == "null") null else codigoString?.toIntOrNull()

            MaestriaFormScreen(
                maestriaViewModel = maestriaViewModel,
                campusViewModel = campusViewModel,
                facultadViewModel = facultadViewModel,
                tipoMaestriaViewModel = tipoMaestriaViewModel,
                maestriaCodigo = codigo,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}