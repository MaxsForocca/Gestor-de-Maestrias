package com.example.gestordemaestrias.ui.screens.campus

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusListScreen(
    viewModel: CampusViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val campusList by viewModel.allCampus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val message by viewModel.message.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCampus by remember { mutableStateOf<Campus?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }

    // Mostrar mensaje si existe
    LaunchedEffect(message) {
        message?.let {
            // El mensaje se muestra en el Snackbar
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Campus") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) }
            ) {
                Icon(Icons.Default.Add, "Agregar Campus")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Buscar campus") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, "Limpiar")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de campus
            val displayList = if (searchQuery.isBlank()) campusList else searchResults

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank())
                            "No hay campus registrados"
                        else
                            "No se encontraron resultados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayList, key = { it.codigo }) { campus ->
                        CampusCard(
                            campus = campus,
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
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Acciones - ${selectedCampus!!.nombre}") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            onNavigateToForm(selectedCampus!!.codigo)
                            showActionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Modificar")
                    }

                    if (selectedCampus!!.estadoRegistro == "A") {
                        TextButton(
                            onClick = {
                                viewModel.inactivateCampus(selectedCampus!!.codigo)
                                showActionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Close, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Inactivar")
                        }
                    } else if (selectedCampus!!.estadoRegistro == "I") {
                        TextButton(
                            onClick = {
                                viewModel.reactivateCampus(selectedCampus!!.codigo)
                                showActionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reactivar")
                        }
                    }

                    TextButton(
                        onClick = {
                            showActionDialog = false
                            showDeleteDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                        Spacer(Modifier.width(8.dp))
                        Text("Eliminar", color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && selectedCampus != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
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
                    Text("Eliminar", color = Color.Red)
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

@Composable
fun CampusCard(
    campus: Campus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = campus.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Código: ${campus.codigo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Indicador de estado
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when (campus.estadoRegistro) {
                            "A" -> Color.Green
                            "I" -> Color.Gray
                            else -> Color.Red
                        },
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}