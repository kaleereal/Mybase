package com.mybase.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybase.app.domain.data.DataEngine
import com.mybase.app.domain.model.EntryEntity
import com.mybase.app.domain.model.FieldEntity
import com.mybase.app.domain.model.FieldType
import com.mybase.app.domain.view.ViewConfig
import com.mybase.app.ui.library.LibraryViewModel
import com.mybase.app.ui.view.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailScreen(
    libraryId: String,
    viewModel: LibraryViewModel,
    onEntryClick: (EntryEntity) -> Unit,
    onAddEntryClick: () -> Unit
) {
    val fields by viewModel.getFieldsByLibrary(libraryId).collectAsState(initial = emptyList())
    val entries by viewModel.getEntriesByLibrary(libraryId).collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddFieldDialog by remember { mutableStateOf(false) }
    var newFieldName by remember { mutableStateOf("") }

    val fieldsMap = remember(fields) { fields.associateBy { it.id } }
    val filteredEntries = remember(entries, fieldsMap, searchQuery) {
        DataEngine.filterEntries(entries, fieldsMap, null, searchQuery)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Library Entries") },
                    actions = {
                        TextButton(onClick = { showAddFieldDialog = true }) {
                            Text("+ Add Field")
                        }
                    }
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search entries...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    singleLine = true
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("List", modifier = Modifier.padding(12.dp)) }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Table", modifier = Modifier.padding(12.dp)) }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) { Text("Grid", modifier = Modifier.padding(12.dp)) }
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) { Text("Kanban", modifier = Modifier.padding(12.dp)) }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntryClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ListViewContent(entries = filteredEntries, fields = fields, onEntryClick = onEntryClick)
                1 -> TableViewContent(entries = filteredEntries, fields = fields, onEntryClick = onEntryClick)
                2 -> GridViewContent(entries = filteredEntries, fields = fields, onEntryClick = onEntryClick)
                3 -> KanbanViewContent(
                    entries = filteredEntries,
                    fields = fields,
                    viewConfig = ViewConfig(),
                    onEntryClick = onEntryClick
                )
            }
        }

        if (showAddFieldDialog) {
            AlertDialog(
                onDismissRequest = { showAddFieldDialog = false },
                title = { Text("Add New Field") },
                text = {
                    OutlinedTextField(
                        value = newFieldName,
                        onValueChange = { newFieldName = it },
                        label = { Text("Field Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFieldName.isNotBlank()) {
                                viewModel.createField(libraryId, newFieldName, FieldType.TEXT)
                                newFieldName = ""
                                showAddFieldDialog = false
                            }
                        }
                    ) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddFieldDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
