package com.example.gestordemaestrias.ui.screens.campus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestordemaestrias.data.entity.Campus
import com.example.gestordemaestrias.ui.viewmodel.CampusViewModel
import com.example.gestordemaestrias.ui.components.ActionDialog
import com.example.gestordemaestrias.ui.components.SimpleInfoCard
import com.example.gestordemaestrias.ui.components.SimpleFilterPanel
import com.example.gestordemaestrias.ui.components.SimpleEmptyState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusListScreen(
    viewModel: CampusViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    // ============= ESTADO =============
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState() //
    var showFilters by remember { mutableStateOf(false) }
    var selectedCampus by remember { mutableStateOf<Campus?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados de filtros
    var filterEstado by remember { mutableStateOf<String?>(null) }

    // ============= DATOS =============
    val campusList by viewModel.allCampus.collectAsState()
    val message by viewModel.message.collectAsState()

    // ============= APLICAR FILTROS ============
    val filteredCampus = remember(
        campusList,
        searchQuery,
        filterEstado
    ){
        campusList.filter { campus ->
            // Filtro por búsqueda
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                campus.nombre.contains(searchQuery, ignoreCase = true)
            }
            // Filtro por estado
            val matchesEstado = filterEstado?.let {
                campus.estadoRegistro == it
            } ?: true

            matchesSearch && matchesEstado
        }
    }

    // Contador de Filtros activos
    val activeFiltersCount = listOfNotNull(
        filterEstado
    ).size


    // Mostrar mensaje si existe
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            // El mensaje se muestra en el Snackbar
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // ======= UI =========
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Campus")
                        if (activeFiltersCount > 0 ) {
                            Text(
                                text = "$activeFiltersCount filtros(s) activo(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Botón de filtros con badge
                    BadgedBox(
                        badge = {
                            if (activeFiltersCount > 0) {
                                Badge { Text("$activeFiltersCount") }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                if (showFilters) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
                                "Filtros"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToForm(null) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nuevo Campus")}
            )
        },
        snackbarHost = {SnackbarHost(snackbarHostState)}
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ========== PANEL DE FILTROS ==========
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SimpleFilterPanel(
                    selectedEstado = filterEstado,
                    onEstadoSelected = { filterEstado = it },
                    onClearFilters = { filterEstado = null }
                )
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar campus") },
                placeholder = { Text("Por nombre de campus")},
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            //Spacer(modifier = Modifier.height(16.dp))

            // ======== RESULTADOS ===============
            //val displayList = if (searchQuery.isBlank()) campusList else searchResults

            if (filteredCampus.isEmpty()) {
                SimpleEmptyState(
                    searchQuery = searchQuery,
                    hasFilters = activeFiltersCount > 0,
                    moduleName = "campus"
                )
            } else {
                // Header con contador
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filteredCampus.size} maestría(s) encontrada(s)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredCampus,
                        key = { it.codigo }
                    ) { campus ->
                        SimpleInfoCard(
                            nombre = campus.nombre,
                            codigo = campus.codigo,
                            estadoRegistro = campus.estadoRegistro,
                            onClick = {
                                selectedCampus = campus
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de acciones
    if (showActionDialog && selectedCampus != null) {
        ActionDialog(
            name = selectedCampus!!.nombre,
            estado = selectedCampus!!.estadoRegistro,
            onDismiss = { showActionDialog = false },
            onEdit = {
                onNavigateToForm(selectedCampus!!.codigo)
                showActionDialog = false
            },
            onDelete = {
                showActionDialog = false
                showDeleteDialog = true
            },
            onInactivate = {
                viewModel.inactivateCampus(selectedCampus!!.codigo)
                showActionDialog = false
            },
            onReactivate = {
                viewModel.reactivateCampus(selectedCampus!!.codigo)
                showActionDialog = false
            }
        )
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && selectedCampus != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar '${selectedCampus!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCampus(selectedCampus!!.codigo)
                        showDeleteDialog = false
                        selectedCampus = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

