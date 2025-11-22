package com.example.gestordemaestrias.ui.screens.maestria

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestordemaestrias.data.dao.MaestriaConRelaciones
import com.example.gestordemaestrias.ui.viewmodel.*
import com.example.gestordemaestrias.ui.components.StatusChip
import com.example.gestordemaestrias.ui.components.ActionDialog
/**
 * Pantalla de lista de Maestrías con sistema de filtros avanzado
 * - Filtro por Tipo de Maestría
 * - Filtro por Facultad
 * - Filtro por Campus
 * - Filtro por Estado (Activo/Inactivo)
 * - Búsqueda por nombre
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaestriaListScreen(
    viewModel: MaestriaViewModel,
    campusViewModel: CampusViewModel,
    facultadViewModel: FacultadViewModel,
    tipoMaestriaViewModel: TipoMaestriaViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    // ========== ESTADO ==========
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<MaestriaConRelaciones?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados de filtros
    var filterTipoMaestria by remember { mutableStateOf<Int?>(null) }
    var filterFacultad by remember { mutableStateOf<Int?>(null) }
    var filterCampus by remember { mutableStateOf<Int?>(null) }
    var filterEstado by remember { mutableStateOf<String?>(null) }

    // ========== DATOS ==========
    val allMaestrias by viewModel.allMaestrias.collectAsState()
    val message by viewModel.message.collectAsState()

    // Listas para filtros
    val tipoMaestriaList by tipoMaestriaViewModel.allTipoMaestria.collectAsState()
    val facultadList by facultadViewModel.allFacultad.collectAsState()
    val campusList by campusViewModel.allCampus.collectAsState()

    // ========== APLICAR FILTROS ==========
    val filteredMaestrias = remember(
        allMaestrias,
        searchQuery,
        filterTipoMaestria,
        filterFacultad,
        filterCampus,
        filterEstado
    ) {
        allMaestrias.filter { maestria ->
            // Filtro por búsqueda
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                maestria.nombre.contains(searchQuery, ignoreCase = true) ||
                        maestria.tipoMaestriaNombre.contains(searchQuery, ignoreCase = true) ||
                        maestria.facultadNombre.contains(searchQuery, ignoreCase = true) ||
                        maestria.campusNombre.contains(searchQuery, ignoreCase = true)
            }

            // Filtro por tipo
            val matchesTipo = filterTipoMaestria?.let {
                maestria.tipoMaestriaCodigo == it
            } ?: true

            // Filtro por facultad
            val matchesFacultad = filterFacultad?.let {
                maestria.facultadCodigo == it
            } ?: true

            // Filtro por campus
            val matchesCampus = filterCampus?.let {
                maestria.campusCodigo == it
            } ?: true

            // Filtro por estado
            val matchesEstado = filterEstado?.let {
                maestria.estadoRegistro == it
            } ?: true

            matchesSearch && matchesTipo && matchesFacultad && matchesCampus && matchesEstado
        }
    }

    // Contador de filtros activos
    val activeFiltersCount = listOfNotNull(
        filterTipoMaestria,
        filterFacultad,
        filterCampus,
        filterEstado
    ).size

    // ========== SNACKBAR PARA MENSAJES ==========
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // ========== UI ==========
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Maestrías")
                        if (activeFiltersCount > 0) {
                            Text(
                                text = "$activeFiltersCount filtro(s) activo(s)",
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
                text = { Text("Nueva Maestría") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ========== PANEL DE FILTROS ==========
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterPanel(
                    tipoMaestriaList = tipoMaestriaList,
                    facultadList = facultadList,
                    campusList = campusList,
                    selectedTipo = filterTipoMaestria,
                    selectedFacultad = filterFacultad,
                    selectedCampus = filterCampus,
                    selectedEstado = filterEstado,
                    onTipoSelected = { filterTipoMaestria = it },
                    onFacultadSelected = { filterFacultad = it },
                    onCampusSelected = { filterCampus = it },
                    onEstadoSelected = { filterEstado = it },
                    onClearFilters = {
                        filterTipoMaestria = null
                        filterFacultad = null
                        filterCampus = null
                        filterEstado = null
                    }
                )
            }

            // ========== BARRA DE BÚSQUEDA ==========
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar maestría") },
                placeholder = { Text("Por nombre, tipo, facultad o campus...") },
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

            // ========== RESULTADOS ==========
            if (filteredMaestrias.isEmpty()) {
                EmptyState(
                    searchQuery = searchQuery,
                    hasFilters = activeFiltersCount > 0
                )
            } else {
                // Header con contador
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filteredMaestrias.size} maestría(s) encontrada(s)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredMaestrias,
                        key = { it.codigo }
                    ) { maestria ->
                        MaestriaCard(
                            maestria = maestria,
                            onClick = {
                                selectedAction = maestria
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // ========== DIÁLOGO DE ACCIONES ==========
    if (showActionDialog && selectedAction != null) {
        ActionDialog(
            name = selectedAction!!.nombre,
            estado = selectedAction!!.estadoRegistro,
            onDismiss = { showActionDialog = false },
            onEdit = {
                onNavigateToForm(selectedAction!!.codigo)
                showActionDialog = false
            },
            onDelete = {
                showActionDialog = false
                showDeleteDialog = true
            },
            onInactivate = {
                // TODO: Implementar inactivar
                viewModel.inactivateMaestria(selectedAction!!.codigo)
                showActionDialog = false
            },
            onReactivate = {
                // TODO: Implementar reactivar
                viewModel.reactivateMaestria(selectedAction!!.codigo)
                showActionDialog = false
            }
        )
    }

    // ========== DIÁLOGO DE CONFIRMACIÓN ELIMINAR ==========
    if (showDeleteDialog && selectedAction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar '${selectedAction!!.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMaestria(selectedAction!!.codigo)
                        showDeleteDialog = false
                        selectedAction = null
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

// ========== COMPONENTE: TARJETA DE MAESTRÍA ==========
@Composable
fun MaestriaCard(
    maestria: MaestriaConRelaciones,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = maestria.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                StatusChip(estado = maestria.estadoRegistro)
            }

            // Etiquetas de información
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Category,
                    label = "Tipo",
                    value = maestria.tipoMaestriaNombre,
                    color = Color(0xFF6200EE)
                )

                InfoChip(
                    icon = Icons.Default.AccountBalance,
                    label = "Facultad",
                    value = maestria.facultadNombre,
                    color = Color(0xFF03DAC5)
                )

                InfoChip(
                    icon = Icons.Default.LocationOn,
                    label = "Campus",
                    value = maestria.campusNombre,
                    color = Color(0xFFFF6F00)
                )
            }
        }
    }
}

// ========== COMPONENTE: CHIP DE INFORMACIÓN ==========
@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ========== COMPONENTE: PANEL DE FILTROS ==========
@Composable
fun FilterPanel(
    tipoMaestriaList: List<com.example.gestordemaestrias.data.entity.TipoMaestria>,
    facultadList: List<com.example.gestordemaestrias.data.entity.Facultad>,
    campusList: List<com.example.gestordemaestrias.data.entity.Campus>,
    selectedTipo: Int?,
    selectedFacultad: Int?,
    selectedCampus: Int?,
    selectedEstado: String?,
    onTipoSelected: (Int?) -> Unit,
    onFacultadSelected: (Int?) -> Unit,
    onCampusSelected: (Int?) -> Unit,
    onEstadoSelected: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onClearFilters) {
                    Icon(Icons.Default.Clear, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Limpiar")
                }
            }

            HorizontalDivider()

            // Filtro por Tipo
            Text(
                text = "Por Tipo de Maestría:",
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tipoMaestriaList) { tipo ->
                    FilterChip(
                        selected = selectedTipo == tipo.codigo,
                        onClick = {
                            onTipoSelected(if (selectedTipo == tipo.codigo) null else tipo.codigo)
                        },
                        label = { Text(tipo.nombre) },
                        leadingIcon = if (selectedTipo == tipo.codigo) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            // Filtro por Facultad
            Text(
                text = "Por Facultad:",
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(facultadList) { facultad ->
                    FilterChip(
                        selected = selectedFacultad == facultad.codigo,
                        onClick = {
                            onFacultadSelected(if (selectedFacultad == facultad.codigo) null else facultad.codigo)
                        },
                        label = { Text(facultad.nombre) },
                        leadingIcon = if (selectedFacultad == facultad.codigo) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            // Filtro por Campus
            Text(
                text = "Por Campus:",
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(campusList) { campus ->
                    FilterChip(
                        selected = selectedCampus == campus.codigo,
                        onClick = {
                            onCampusSelected(if (selectedCampus == campus.codigo) null else campus.codigo)
                        },
                        label = { Text(campus.nombre) },
                        leadingIcon = if (selectedCampus == campus.codigo) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            // Filtro por Estado
            Text(
                text = "Por Estado:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedEstado == "A",
                    onClick = { onEstadoSelected(if (selectedEstado == "A") null else "A") },
                    label = { Text("Activos") },
                    leadingIcon = if (selectedEstado == "A") {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = selectedEstado == "I",
                    onClick = { onEstadoSelected(if (selectedEstado == "I") null else "I") },
                    label = { Text("Inactivos") },
                    leadingIcon = if (selectedEstado == "I") {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

// ========== COMPONENTE: ESTADO VACÍO ==========
@Composable
fun EmptyState(searchQuery: String, hasFilters: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "No se encontraron maestrías"
                    hasFilters -> "No hay resultados con estos filtros"
                    else -> "No hay maestrías registradas"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "Intenta con otra búsqueda"
                    hasFilters -> "Prueba ajustando los filtros"
                    else -> "Agrega tu primera maestría"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

