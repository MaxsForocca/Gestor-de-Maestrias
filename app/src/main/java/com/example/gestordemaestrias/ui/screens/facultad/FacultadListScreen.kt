package com.example.gestordemaestrias.ui.screens.facultad
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
import com.example.gestordemaestrias.ui.components.ActionDialog
import com.example.gestordemaestrias.ui.components.SimpleInfoCard
import com.example.gestordemaestrias.ui.components.SimpleFilterPanel
import com.example.gestordemaestrias.ui.components.SimpleEmptyState
import com.example.gestordemaestrias.ui.viewmodel.FacultadViewModel
import com.example.gestordemaestrias.data.entity.Facultad


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultadListScreen(
    viewModel: FacultadViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    // ============= ESTADO =============
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState() //
    var showFilters by remember { mutableStateOf(false) }
    var selectedFacultad by remember { mutableStateOf<Facultad?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados de filtros
    var filterEstado by remember { mutableStateOf<String?>(null) }

    // ============= DATOS =============
    val facultadList by viewModel.allFacultad.collectAsState()
    val message by viewModel.message.collectAsState()

    // ============= APLICAR FILTROS ============
    val filteredFacultad = remember(
        facultadList,
        searchQuery,
        filterEstado
    ){
        facultadList.filter { facultad ->
            // Filtro por búsqueda
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                facultad.nombre.contains(searchQuery, ignoreCase = true)
            }
            // Filtro por estado
            val matchesEstado = filterEstado?.let {
                facultad.estadoRegistro == it
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
                        Text("Facultad")
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
                text = { Text("Nueva Facultad")}
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
                label = { Text("Buscar facultad") },
                placeholder = { Text("Por nombre de facultad")},
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
            //val displayList = if (searchQuery.isBlank()) facultadList else searchResults

            if (filteredFacultad.isEmpty()) {
                SimpleEmptyState(
                    searchQuery = searchQuery,
                    hasFilters = activeFiltersCount > 0,
                    moduleName = "facultad"
                )
            } else {
                // Header con contador
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filteredFacultad.size} maestría(s) encontrada(s)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredFacultad,
                        key = { it.codigo }
                    ) { facultad ->
                        SimpleInfoCard(
                            nombre = facultad.nombre,
                            codigo = facultad.codigo,
                            estadoRegistro = facultad.estadoRegistro,
                            onClick = {
                                selectedFacultad = facultad
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de acciones
    if (showActionDialog && selectedFacultad != null) {
        ActionDialog(
            name = selectedFacultad!!.nombre,
            estado = selectedFacultad!!.estadoRegistro,
            onDismiss = { showActionDialog = false },
            onEdit = {
                onNavigateToForm(selectedFacultad!!.codigo)
                showActionDialog = false
            },
            onDelete = {
                showActionDialog = false
                showDeleteDialog = true
            },
            onInactivate = {
                viewModel.inactivateFacultad(selectedFacultad!!.codigo)
                showActionDialog = false
            },
            onReactivate = {
                viewModel.reactivateFacultad(selectedFacultad!!.codigo)
                showActionDialog = false
            }
        )
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && selectedFacultad != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar '${selectedFacultad!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFacultad(selectedFacultad!!.codigo)
                        showDeleteDialog = false
                        selectedFacultad = null
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

