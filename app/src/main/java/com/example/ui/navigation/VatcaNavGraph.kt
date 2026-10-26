package com.example.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.VatcaApplication
import com.example.ui.library.LibraryScreen
import com.example.ui.library.LibraryViewModel
import com.example.ui.reader.ReaderScreen
import com.example.ui.reader.ReaderViewModel
import com.example.ui.settings.SettingsScreen

object Destinations {
    const val LIBRARY = "library"
    const val READER = "reader/{documentId}"
    const val SETTINGS = "settings"

    fun reader(documentId: String): String = "reader/${Uri.encode(documentId)}"
}

@Composable
fun VatcaNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as VatcaApplication

    NavHost(
        navController = navController,
        startDestination = Destinations.LIBRARY,
        modifier = modifier
    ) {
        composable(Destinations.LIBRARY) {
            val libraryViewModel: LibraryViewModel = viewModel(
                factory = LibraryViewModel.Factory(
                    repository = app.repository,
                    preferencesManager = app.preferencesManager
                )
            )

            LibraryScreen(
                viewModel = libraryViewModel,
                onOpenDocument = { docId ->
                    navController.navigate(Destinations.reader(docId))
                },
                onOpenSettings = {
                    navController.navigate(Destinations.SETTINGS)
                }
            )
        }

        composable(
            route = Destinations.READER,
            arguments = listOf(
                navArgument("documentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            val readerViewModel: ReaderViewModel = viewModel(
                key = "reader_$documentId",
                factory = ReaderViewModel.Factory(
                    documentId = documentId,
                    context = context,
                    repository = app.repository,
                    preferencesManager = app.preferencesManager
                )
            )

            ReaderScreen(
                viewModel = readerViewModel,
                onBackToLibrary = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                preferencesManager = app.preferencesManager,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
