package com.svbackend.natai.android

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.svbackend.natai.android.ui.screen.MainScreen
import com.svbackend.natai.android.ui.screen.NewNoteScreen
import com.svbackend.natai.android.ui.screen.NoteDetailsScreen


@Composable
fun Navigation(controller: NavHostController) {

    NavHost(navController = controller, startDestination = Route.MainRoute.route) {
        composable(route = Route.MainRoute.route) {
            MainScreen()
        }

        composable(route = Route.NewNoteRoute.route) {
            NewNoteScreen(onSuccess = {
                controller.navigate(Route.MainRoute.withArgs())
            })
        }

        composable(
            route = Route.NoteDetailsRoute.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            NoteDetailsScreen(
                noteId = entry.arguments!!["noteId"] as String
            )
        }
    }
}