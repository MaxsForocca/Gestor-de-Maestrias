package com.example.gestordemaestrias.ui.screens.tipomaestria

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
import com.example.gestordemaestrias.data.entity.TipoMaestria
import com.example.gestordemaestrias.ui.viewmodel.TipoMaestriaViewModel
import com.example.gestordemaestrias.ui.components.ActionDialog
import com.example.gestordemaestrias.ui.components.SimpleInfoCard
import com.example.gestordemaestrias.ui.components.SimpleFilterPanel
import com.example.gestordemaestrias.ui.components.SimpleEmptyState
import com.example.gestordemaestrias.ui.components.SimpleSortMenu
import com.example.gestordemaestrias.ui.components.SortOption


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoMaestriaListScreen(
    viewModel: TipoMaestriaViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    // ============= ESTADO =============
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState() //
    var showFilters by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedTipoMaestria by remember { mutableStateOf<TipoMaestria?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados de filtros
    var filterEstado by remember { mutableStateOf<String?>(null) }

    // Estado de Ordenamiento
    var sortOption by remember { mutableStateOf(SortOption.NOMBRE_ASC) }

    // ============= DATOS =============
    val tipoMaestriaList by viewModel.allTipoMaestria.collectAsState()
    val message by viewModel.message.collectAsState()

    // ============= APLICAR FILTROS ============
    val filteredTipoMaestria = remember(
        tipoMaestriaList,
        searchQuery,
        filterEstado,
        sortOption
    ){
        val filtered = tipoMaestriaList.filter { tipoMaestria ->
            // Filtro por búsqueda
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                tipoMaestria.nombre.contains(searchQuery, ignoreCase = true)
            }
            // Filtro por estado
            val matchesEstado = filterEstado?.let {
                tipoMaestria.estadoRegistro == it
            } ?: true

            matchesSearch && matchesEstado
        }
        when (sortOption){
            SortOption.CODIGO_ASC -> filtered.sortedBy { it.codigo }
            SortOption.CODIGO_DESC -> filtered.sortedByDescending { it.codigo }
            SortOption.NOMBRE_ASC -> filtered.sortedBy { it.nombre.lowercase() }
            SortOption.NOMBRE_DESC -> filtered.sortedByDescending { it.nombre.lowercase() }
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
                        Text(
                            "Tipo de Maestria",
                            fontWeight = FontWeight.Bold
                        )
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
                    // Boton de ordenamiento
                    IconButton(onClick = { showSortMenu = true}) {
                        Icon(Icons.Default.Sort, "Ordenar")
                    }
                    // Menu de Ordenamiento
                    SimpleSortMenu(
                        expanded = showSortMenu,
                        selectedOption = sortOption,
                        onOptionSelected = {sortOption=it},
                        onDismiss = {showSortMenu=false}
                    )
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
                text = { Text("Nuevo Cmapus")}
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
                label = { Text("Buscar tipo maestria") },
                placeholder = { Text("Por nombre de tipo de maestria")},
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
            //val displayList = if (searchQuery.isBlank()) tipoMaestriaList else searchResults

            if (filteredTipoMaestria.isEmpty()) {
                SimpleEmptyState(
                    searchQuery = searchQuery,
                    hasFilters = activeFiltersCount > 0,
                    moduleName = "tipo de maestria"
                )
            } else {
                // Header con contador
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filteredTipoMaestria.size} tipo(s) de maestria(s) encontrada(s)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredTipoMaestria,
                        key = { it.codigo }
                    ) { tipoMaestria ->
                        SimpleInfoCard(
                            nombre = tipoMaestria.nombre,
                            codigo = tipoMaestria.codigo,
                            estadoRegistro = tipoMaestria.estadoRegistro,
                            onClick = {
                                selectedTipoMaestria = tipoMaestria
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de acciones
    if (showActionDialog && selectedTipoMaestria != null) {
        ActionDialog(
            name = selectedTipoMaestria!!.nombre,
            estado = selectedTipoMaestria!!.estadoRegistro,
            onDismiss = { showActionDialog = false },
            onEdit = {
                onNavigateToForm(selectedTipoMaestria!!.codigo)
                showActionDialog = false
            },
            onDelete = {
                showActionDialog = false
                showDeleteDialog = true
            },
            onInactivate = {
                viewModel.inactivateTipoMaestria(selectedTipoMaestria!!.codigo)
                showActionDialog = false
            },
            onReactivate = {
                viewModel.reactivateTipoMaestria(selectedTipoMaestria!!.codigo)
                showActionDialog = false
            }
        )
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && selectedTipoMaestria != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar '${selectedTipoMaestria!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTipoMaestria(selectedTipoMaestria!!.codigo)
                        showDeleteDialog = false
                        selectedTipoMaestria = null
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

