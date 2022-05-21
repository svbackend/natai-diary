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
import com.svbackend.natai.android.utils.go
import com.svbackend.natai.android.viewmodel.NoteViewModel


@Composable
fun Navigation(controller: NavHostController, vm: NoteViewModel, onLoginClick: () -> Unit) {

    NavHost(navController = controller, startDestination = Route.MainRoute.route) {
        composable(route = Route.MainRoute.route) {
            MainScreen(
                vm = vm,
                onAddClick = {
                             controller.go(Route.NewNoteRoute.route)
                },
                onLoginClick = onLoginClick
            )
        }

        composable(route = Route.NewNoteRoute.route) {
            NewNoteScreen(onSuccess = {
                controller.popBackStack()
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