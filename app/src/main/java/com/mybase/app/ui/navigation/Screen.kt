package com.mybase.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Workspaces : Screen("workspaces", "Workspaces", Icons.Default.Folder)
    object WorkspaceDetail : Screen("workspace/{workspaceId}", "Workspace Detail") {
        fun createRoute(workspaceId: String) = "workspace/$workspaceId"
    }
    object LibraryDetail : Screen("library/{libraryId}?viewId={viewId}", "Library Detail") {
        fun createRoute(libraryId: String, viewId: String? = null) =
            if (viewId != null) "library/$libraryId?viewId=$viewId" else "library/$libraryId"
    }
    object EntryDetail : Screen("entry/{entryId}?libraryId={libraryId}", "Entry Detail") {
        fun createRoute(entryId: String, libraryId: String) = "entry/$entryId?libraryId=$libraryId"
    }
    object EntryEdit : Screen("entry_edit/{libraryId}?entryId={entryId}", "Edit Entry") {
        fun createRoute(libraryId: String, entryId: String? = null) =
            if (entryId != null) "entry_edit/$libraryId?entryId=$entryId" else "entry_edit/$libraryId"
    }
    object Dashboard : Screen("dashboard/{workspaceId}", "Dashboard", Icons.Default.Dashboard) {
        fun createRoute(workspaceId: String) = "dashboard/$workspaceId"
    }
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object ScriptConsole : Screen("script_console/{libraryId}", "Scripting Engine") {
        fun createRoute(libraryId: String) = "script_console/$libraryId"
    }
}
