package com.example.gestordemaestrias.ui.screens.campus

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
import com.example.gestordemaestrias.ui.viewmodel.CampusViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusFormScreen(
    viewModel: CampusViewModel,
    campusCodigo: Int?,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val message by viewModel.message.collectAsState()

    val isEditMode = campusCodigo != null

    // Cargar datos si es modo edición
    LaunchedEffect(campusCodigo) {
        if (campusCodigo != null) {
            isLoading = true
            val campus = viewModel.getCampusByCodigo(campusCodigo)
            if (campus != null) {
                nombre = campus.nombre
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
                    Text(
                        if (isEditMode) "Editar Campus" else "Nuevo Campus",
                        fontWeight = FontWeight.Bold
                    )},
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
                        label = { Text("Nombre del Campus") },
                        placeholder = { Text("Ej: Biomedicas") },
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
                                    if (isEditMode && campusCodigo != null) {
                                        viewModel.updateCampus(campusCodigo, nombre)
                                    } else {
                                        viewModel.insertCampus(nombre)
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