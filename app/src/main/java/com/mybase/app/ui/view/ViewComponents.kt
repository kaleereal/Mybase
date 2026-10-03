package com.mybase.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybase.app.domain.data.DataEngine
import com.mybase.app.domain.model.EntryEntity
import com.mybase.app.domain.model.FieldEntity
import com.mybase.app.domain.view.ViewConfig

@Composable
fun ListViewContent(
    entries: List<EntryEntity>,
    fields: List<FieldEntity>,
    onEntryClick: (EntryEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(entries, key = { it.id }) { entry ->
            val values = DataEngine.parseValues(entry.fieldValuesJson)
            val primaryField = fields.firstOrNull()
            val title = primaryField?.let { values[it.id] } ?: "Entry ${entry.id.take(4)}"

            Card(
                onClick = { onEntryClick(entry) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    fields.drop(1).take(2).forEach { field ->
                        val valStr = values[field.id] ?: ""
                        if (valStr.isNotBlank()) {
                            Text(
                                text = "${field.name}: $valStr",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableViewContent(
    entries: List<EntryEntity>,
    fields: List<FieldEntity>,
    onEntryClick: (EntryEntity) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
        Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
            fields.forEach { field ->
                Text(
                    text = field.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.width(120.dp).padding(4.dp)
                )
            }
        }

        LazyColumn {
            items(entries, key = { it.id }) { entry ->
                val values = DataEngine.parseValues(entry.fieldValuesJson)
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    fields.forEach { field ->
                        Text(
                            text = values[field.id] ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(120.dp).padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GridViewContent(
    entries: List<EntryEntity>,
    fields: List<FieldEntity>,
    onEntryClick: (EntryEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        items(entries, key = { it.id }) { entry ->
            val values = DataEngine.parseValues(entry.fieldValuesJson)
            val primaryField = fields.firstOrNull()
            val title = primaryField?.let { values[it.id] } ?: "Entry ${entry.id.take(4)}"

            Card(
                onClick = { onEntryClick(entry) },
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall)
                    fields.drop(1).take(2).forEach { field ->
                        Text(
                            text = values[field.id] ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanViewContent(
    entries: List<EntryEntity>,
    fields: List<FieldEntity>,
    viewConfig: ViewConfig,
    onEntryClick: (EntryEntity) -> Unit
) {
    val statusFieldId = viewConfig.kanbanStatusFieldId ?: fields.firstOrNull()?.id ?: ""
    val grouped = DataEngine.groupEntries(entries, statusFieldId)
    val scrollState = rememberScrollState()

    Row(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState).padding(8.dp)) {
        grouped.forEach { (statusGroup, groupEntries) ->
            Card(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "$statusGroup (${groupEntries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    LazyColumn {
                        items(groupEntries, key = { it.id }) { entry ->
                            val values = DataEngine.parseValues(entry.fieldValuesJson)
                            val title = fields.firstOrNull()?.let { values[it.id] } ?: entry.id

                            Card(
                                onClick = { onEntryClick(entry) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
