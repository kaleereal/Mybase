package com.mybase.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mybase.app.data.db.MyBaseDatabase
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.field.EntryManager
import com.mybase.app.ui.library.LibraryViewModel
import com.mybase.app.ui.navigation.Screen
import com.mybase.app.ui.screens.*
import com.mybase.app.ui.theme.MyBaseTheme
import com.mybase.app.ui.workspace.WorkspaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = MyBaseDatabase.getDatabase(this)
        val repository = MyBaseRepository(
            db.workspaceDao(),
            db.libraryDao(),
            db.fieldDao(),
            db.entryDao(),
            db.relationDao(),
            db.viewDao(),
            db.automationDao(),
            db.dashboardWidgetDao(),
            db.attachmentDao()
        )
        val entryManager = EntryManager(repository)
        val workspaceViewModel = WorkspaceViewModel(repository)
        val libraryViewModel = LibraryViewModel(repository, entryManager)

        setContent {
            MyBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Workspaces.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Workspaces.route) {
                                WorkspacesScreen(
                                    viewModel = workspaceViewModel,
                                    onWorkspaceClick = { workspace ->
                                        navController.navigate(Screen.WorkspaceDetail.createRoute(workspace.id))
                                    }
                                )
                            }
                            composable(
                                route = Screen.WorkspaceDetail.route,
                                arguments = listOf(navArgument("workspaceId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: ""
                                WorkspaceDetailScreen(
                                    workspaceId = workspaceId,
                                    viewModel = libraryViewModel,
                                    onLibraryClick = { library ->
                                        navController.navigate(Screen.LibraryDetail.createRoute(library.id))
                                    }
                                )
                            }
                            composable(
                                route = Screen.LibraryDetail.route,
                                arguments = listOf(navArgument("libraryId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val libraryId = backStackEntry.arguments?.getString("libraryId") ?: ""
                                LibraryDetailScreen(
                                    libraryId = libraryId,
                                    viewModel = libraryViewModel,
                                    onEntryClick = { entry ->
                                        navController.navigate(Screen.EntryEdit.createRoute(libraryId, entry.id))
                                    },
                                    onAddEntryClick = {
                                        navController.navigate(Screen.EntryEdit.createRoute(libraryId))
                                    }
                                )
                            }
                            composable(
                                route = Screen.EntryEdit.route,
                                arguments = listOf(
                                    navArgument("libraryId") { type = NavType.StringType },
                                    navArgument("entryId") { type = NavType.StringType; nullable = true }
                                )
                            ) { backStackEntry ->
                                val libraryId = backStackEntry.arguments?.getString("libraryId") ?: ""
                                val entryId = backStackEntry.arguments?.getString("entryId")
                                EntryEditScreen(
                                    libraryId = libraryId,
                                    entryId = entryId,
                                    viewModel = libraryViewModel,
                                    onSaveSuccess = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
