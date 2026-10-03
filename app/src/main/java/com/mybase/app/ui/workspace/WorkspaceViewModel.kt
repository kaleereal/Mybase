package com.mybase.app.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.WorkspaceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class WorkspaceViewModel(private val repository: MyBaseRepository) : ViewModel() {

    val workspaces: StateFlow<List<WorkspaceEntity>> = repository.getAllWorkspaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createWorkspace(name: String, description: String = "", icon: String = "folder", color: String = "#6200EE") {
        viewModelScope.launch {
            val workspace = WorkspaceEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                icon = icon,
                color = color
            )
            repository.insertWorkspace(workspace)
        }
    }

    fun updateWorkspace(workspace: WorkspaceEntity) {
        viewModelScope.launch {
            repository.updateWorkspace(workspace.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteWorkspace(workspace: WorkspaceEntity) {
        viewModelScope.launch {
            repository.deleteWorkspace(workspace)
        }
    }
}
