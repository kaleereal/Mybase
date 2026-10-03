package com.mybase.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybase.app.domain.data.DataEngine
import com.mybase.app.ui.library.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditScreen(
    libraryId: String,
    entryId: String?,
    viewModel: LibraryViewModel,
    onSaveSuccess: () -> Unit
) {
    val fields by viewModel.getFieldsByLibrary(libraryId).collectAsState(initial = emptyList())
    val entries by viewModel.getEntriesByLibrary(libraryId).collectAsState(initial = emptyList())
    val entryValues = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(entryId, entries) {
        if (entryId != null) {
            val existingEntry = entries.find { it.id == entryId }
            if (existingEntry != null) {
                val parsed = DataEngine.parseValues(existingEntry.fieldValuesJson)
                parsed.forEach { (k, v) -> entryValues[k] = v }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null) "New Entry" else "Edit Entry") }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            viewModel.saveEntry(libraryId, entryId, entryValues) { result ->
                                if (result.isSuccess) {
                                    onSaveSuccess()
                                }
                            }
                        }
                    ) {
                        Text("Save Entry")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(fields, key = { it.id }) { field ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = field.name, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = entryValues[field.id] ?: "",
                        onValueChange = { entryValues[field.id] = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}
