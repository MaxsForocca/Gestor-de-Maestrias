package com.example.gestordemaestrias.ui.screens.tipomaestria


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestordemaestrias.ui.viewmodel.TipoMaestriaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoMaestriaFormScreen(
    viewModel: TipoMaestriaViewModel,
    tipoMaestriaCodigo: Int?,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val message by viewModel.message.collectAsState()

    val isEditMode = tipoMaestriaCodigo != null

    // Cargar datos si es modo edición
    LaunchedEffect(tipoMaestriaCodigo) {
        if (tipoMaestriaCodigo != null) {
            isLoading = true
            val tipoMaestria = viewModel.getTipoMaestriaByCodigo(tipoMaestriaCodigo)
            if (tipoMaestria != null) {
                nombre = tipoMaestria.nombre
            }
            isLoading = false
        }
    }

    // Navegar de vuelta si se guardó exitosamente
    LaunchedEffect(message) {
        if (message?.contains("exitosamente") == true) {
            kotlinx.coroutines.delay(500)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Editar Tipo de Maestria" else "Nuevo Tipo de Maestria",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo de texto para nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Tipo de Maestria") },
                        placeholder = { Text("Ej: Maestria Habilitante") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Mensaje de error/éxito
                    message?.let {
                        Text(
                            text = it,
                            color = if (it.contains("Error"))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    if (isEditMode && tipoMaestriaCodigo != null) {
                                        viewModel.updateTipoMaestria(tipoMaestriaCodigo, nombre)
                                    } else {
                                        viewModel.insertTipoMaestria(nombre)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = nombre.isNotBlank()
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
}