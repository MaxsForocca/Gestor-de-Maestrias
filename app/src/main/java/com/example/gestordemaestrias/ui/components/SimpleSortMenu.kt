package com.example.gestordemaestrias.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SortOption(
    val label: String,
    val shortLabel: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    CODIGO_ASC("Código (Menor a Mayor)", "Código ↑", Icons.Default.ArrowUpward),
    CODIGO_DESC("Código (Mayor a Menor)", "Código ↓", Icons.Default.ArrowDownward),
    NOMBRE_ASC("Nombre (A-Z)", "Nombre A-Z", Icons.Default.SortByAlpha),
    NOMBRE_DESC("Nombre (Z-A)", "Nombre Z-A", Icons.Default.SortByAlpha)
}

@Composable
fun SimpleSortMenu(
    expanded: Boolean,
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
){
    // Menu de Ordenamiento
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        Text(
            text = "Ordenar por:",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider()
        SortOption.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(option.label)
                    }
                },
                onClick = {
                    onOptionSelected(option)
                    onDismiss()
                },
                leadingIcon = {
                    if (selectedOption == option) {
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
