package com.mybase.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybase.app.domain.model.LibraryEntity
import com.mybase.app.ui.library.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDetailScreen(
    workspaceId: String,
    viewModel: LibraryViewModel,
    onLibraryClick: (LibraryEntity) -> Unit
) {
    val libraries by viewModel.getLibrariesByWorkspace(workspaceId).collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newLibraryName by remember { mutableStateOf("") }
    var newLibraryDesc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Workspace Libraries") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Library")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (libraries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Libraries in this workspace. Tap '+' to create one.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(libraries, key = { it.id }) { library ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onLibraryClick(library) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.GridOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(library.name, style = MaterialTheme.typography.titleMedium)
                                    if (library.description.isNotBlank()) {
                                        Text(library.description, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteLibrary(library) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Create Library") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newLibraryName,
                            onValueChange = { newLibraryName = it },
                            label = { Text("Library Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newLibraryDesc,
                            onValueChange = { newLibraryDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newLibraryName.isNotBlank()) {
                                viewModel.createLibrary(workspaceId, newLibraryName, newLibraryDesc)
                                newLibraryName = ""
                                newLibraryDesc = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
