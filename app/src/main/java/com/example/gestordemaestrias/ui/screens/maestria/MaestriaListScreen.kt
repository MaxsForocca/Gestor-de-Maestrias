package com.example.gestordemaestrias.ui.screens.maestria


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestordemaestrias.data.dao.MaestriaConRelaciones
import com.example.gestordemaestrias.data.entity.Maestria
import com.example.gestordemaestrias.ui.viewmodel.MaestriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaestriaListScreen(
    viewModel: MaestriaViewModel,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val maestriaList by viewModel.allMaestrias.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val message by viewModel.message.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedMaestria by remember { mutableStateOf<MaestriaConRelaciones?>(null) }
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
                title = { Text("Gestión de Maestria") },
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
                Icon(Icons.Default.Add, "Agregar Maestria")
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
                label = { Text("Buscar Maestria") },
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

            // Lista de Maestria
            val displayList = if (searchQuery.isBlank()) maestriaList else searchResults

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank())
                            "No hay Maestrias registradas"
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
                    items(displayList, key = { it.codigo }) { maestria ->
                        MaestriaCard(
                            maestria = maestria,
                            onClick = {
                                selectedMaestria = maestria
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogo de Acciones
    if (showActionDialog && selectedMaestria != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Acciones - ${selectedMaestria!!.nombre}") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            onNavigateToForm(selectedMaestria!!.codigo)
                            showActionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Modificar")
                    }

                    if (selectedMaestria!!.estadoRegistro == "A") {
                        TextButton(
                            onClick = {
                                viewModel.inactivateMaestria(selectedMaestria!!.codigo)
                                showActionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Close, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Inactivar")
                        }
                    } else if (selectedMaestria!!.estadoRegistro == "I") {
                        TextButton(
                            onClick = {
                                viewModel.reactivateMaestria(selectedMaestria!!.codigo)
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
    if (showDeleteDialog && selectedMaestria != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar '${selectedMaestria!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMaestria(selectedMaestria!!.codigo)
                        showDeleteDialog = false
                        selectedMaestria = null
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
fun MaestriaCard(
    maestria: MaestriaConRelaciones,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = maestria.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Codigo: ${maestria.codigo}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}