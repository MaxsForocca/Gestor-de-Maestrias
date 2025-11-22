package com.example.gestordemaestrias.ui.screens.maestria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestordemaestrias.data.entity.*
import com.example.gestordemaestrias.ui.viewmodel.*
import kotlinx.coroutines.launch

/**
 * Formulario para crear/editar Maestrías
 * Usa listas de selección (ExposedDropdownMenu) para las relaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaestriaFormScreen(
    maestriaViewModel: MaestriaViewModel,
    campusViewModel: CampusViewModel,
    facultadViewModel: FacultadViewModel,
    tipoMaestriaViewModel: TipoMaestriaViewModel,
    maestriaCodigo: Int?,
    onNavigateBack: () -> Unit
) {
    // ========== ESTADO LOCAL ==========
    var nombre by remember { mutableStateOf("") }
    var selectedTipoMaestria by remember { mutableStateOf<TipoMaestria?>(null) }
    var selectedFacultad by remember { mutableStateOf<Facultad?>(null) }
    var selectedCampus by remember { mutableStateOf<Campus?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // ========== DATOS DE VIEWMODELS ==========
    val tipoMaestriaList by tipoMaestriaViewModel.activeTipoMaestria.collectAsState()
    val facultadList by facultadViewModel.activeFacultad.collectAsState()
    val campusList by campusViewModel.activeCampus.collectAsState()
    val message by maestriaViewModel.message.collectAsState()

    val isEditMode = maestriaCodigo != null

    // ========== VALIDACIÓN REACTIVA ==========
    val isValid by remember {
        derivedStateOf {
            nombre.isNotBlank() &&
                    selectedTipoMaestria != null &&
                    selectedFacultad != null &&
                    selectedCampus != null
        }
    }

    val nombreError by remember {
        derivedStateOf {
            when {
                nombre.isBlank() -> null
                nombre.length < 3 -> "Mínimo 3 caracteres"
                else -> null
            }
        }
    }

    // ========== CARGAR DATOS EN MODO EDICIÓN ==========
    LaunchedEffect(maestriaCodigo) {
        if (maestriaCodigo != null) {
            isLoading = true
            maestriaViewModel.getMaestriaByCodigo(maestriaCodigo)?.let { maestria ->
                nombre = maestria.nombre
                selectedTipoMaestria = tipoMaestriaViewModel.getTipoMaestriaByCodigo(maestria.tipoMaestriaCodigo)
                selectedFacultad = facultadViewModel.getFacultadByCodigo(maestria.facultadCodigo)
                selectedCampus = campusViewModel.getCampusByCodigo(maestria.campusCodigo)
            }
            isLoading = false
        }
    }

    // ========== NAVEGACIÓN DESPUÉS DE GUARDAR ==========
    LaunchedEffect(message) {
        if (message?.contains("exitosamente") == true) {
            kotlinx.coroutines.delay(1000)
            onNavigateBack()
        }
    }

    // ========== UI ==========
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Editar Maestría" else "Nueva Maestría")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Cargando datos...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ========== TÍTULO DE SECCIÓN ==========
                Text(
                    text = "Información General",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // ========== CAMPO: NOMBRE ==========
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Maestría") },
                    placeholder = { Text("Ej: Ingeniería de Sistemas") },
                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                    isError = nombreError != null && nombre.isNotBlank(),
                    supportingText = if (nombreError != null && nombre.isNotBlank()) {
                        { Text(nombreError!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ========== TÍTULO DE SECCIÓN ==========
                Text(
                    text = "Referencias (Seleccione de las listas)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // ========== SELECTOR: TIPO DE MAESTRÍA ==========
                DropdownSelector(
                    label = "Tipo de Maestría",
                    items = tipoMaestriaList,
                    selectedItem = selectedTipoMaestria,
                    onItemSelected = { selectedTipoMaestria = it },
                    itemLabel = { it.nombre },
                    icon = Icons.Default.Category,
                    isError = selectedTipoMaestria == null && nombre.isNotBlank(),
                    errorMessage = if (selectedTipoMaestria == null) "Requerido" else null
                )

                // ========== SELECTOR: FACULTAD ==========
                DropdownSelector(
                    label = "Facultad",
                    items = facultadList,
                    selectedItem = selectedFacultad,
                    onItemSelected = { selectedFacultad = it },
                    itemLabel = { it.nombre },
                    icon = Icons.Default.AccountBalance,
                    isError = selectedFacultad == null && nombre.isNotBlank(),
                    errorMessage = if (selectedFacultad == null) "Requerido" else null
                )

                // ========== SELECTOR: CAMPUS ==========
                DropdownSelector(
                    label = "Campus",
                    items = campusList,
                    selectedItem = selectedCampus,
                    onItemSelected = { selectedCampus = it },
                    itemLabel = { it.nombre },
                    icon = Icons.Default.LocationOn,
                    isError = selectedCampus == null && nombre.isNotBlank(),
                    errorMessage = if (selectedCampus == null) "Requerido" else null
                )

                // ========== MENSAJE DE ERROR/ÉXITO ==========
                message?.let {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (it.contains("Error"))
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (it.contains("Error"))
                                    Icons.Default.Warning
                                else
                                    Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (it.contains("Error"))
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ========== RESUMEN DE SELECCIÓN ==========
                if (isValid) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Resumen",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Text("✓ Nombre: $nombre")
                            Text("✓ Tipo: ${selectedTipoMaestria?.nombre}")
                            Text("✓ Facultad: ${selectedFacultad?.nombre}")
                            Text("✓ Campus: ${selectedCampus?.nombre}")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ========== BOTONES DE ACCIÓN ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                if (isEditMode && maestriaCodigo != null) {
                                    maestriaViewModel.updateMaestria(
                                        codigo = maestriaCodigo,
                                        nombre = nombre.trim(),
                                        tipoMaestriaCodigo = selectedTipoMaestria!!.codigo,
                                        facultadCodigo = selectedFacultad!!.codigo,
                                        campusCodigo = selectedCampus!!.codigo
                                    )
                                } else {
                                    maestriaViewModel.insertMaestria(
                                        nombre = nombre.trim(),
                                        tipoMaestriaCodigo = selectedTipoMaestria!!.codigo,
                                        facultadCodigo = selectedFacultad!!.codigo,
                                        campusCodigo = selectedCampus!!.codigo
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

// ========== COMPONENTE: DROPDOWN SELECTOR ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && items.isNotEmpty() },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem?.let(itemLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Seleccionar...") },
            leadingIcon = { Icon(icon, null) },
            trailingIcon = {
                if (items.isEmpty()) {
                    Icon(
                        Icons.Default.Warning,
                        "Sin datos",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            },
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else if (items.isEmpty()) {
                { Text("No hay opciones disponibles", color = MaterialTheme.colorScheme.error) }
            } else null,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        if (items.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                itemLabel(item),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        leadingIcon = {
                            if (selectedItem == item) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}