package com.svbackend.natai.android

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.svbackend.natai.android.ui.screen.*
import com.svbackend.natai.android.ui.screen.auth.*
import com.svbackend.natai.android.ui.screen.settings.AppInfoScreen
import com.svbackend.natai.android.ui.screen.settings.FeedbackScreen
import com.svbackend.natai.android.ui.screen.settings.ReminderScreen
import com.svbackend.natai.android.ui.screen.settings.ThemesScreen
import com.svbackend.natai.android.utils.go
import com.svbackend.natai.android.viewmodel.EditNoteViewModel
import com.svbackend.natai.android.viewmodel.NoteViewModel


@Composable
fun Navigation(
    controller: NavHostController,
    vm: NoteViewModel,
    editNoteViewModel: EditNoteViewModel = viewModel(),
    onAskForNotificationPermission: () -> Unit
) {

    NavHost(navController = controller, startDestination = Route.MainRoute.route) {
        composable(
            route = Route.MainRoute.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://natai.app/diary"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            MainScreen(
                vm = vm,
                onAddClick = {
                    controller.go(Route.NewNoteRoute.withArgs())
                },
                onLoginClick = {
                    controller.go(Route.LoginRoute.withArgs())
                },
                onRegisterClick = {
                    controller.go(Route.RegistrationRoute.withArgs())
                },
                onNoteClick = {
                    vm.selectNote(it.id)
                    controller.go(Route.NoteDetailsRoute.withArgs(noteId = it.id))
                }
            )
        }

        composable(route = Route.NewNoteRoute.route) {
            NewNoteScreen(
                onSuccess = {
                    controller.popBackStack()
                }
            )
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
                vm = vm,
                noteId = entry.arguments?.getString("noteId") ?: "",
                onEditClick = {
                    editNoteViewModel.loadNote(it)
                    controller.go(Route.EditNoteRoute.withArgs(noteId = it))
                },
                onDeleteClick = {
                    vm.deleteNote(it)
                    controller.popBackStack()
                }
            )
        }

        composable(
            route = Route.EditNoteRoute.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                }
            )
        ) {
            EditNoteScreen(
                vm = editNoteViewModel,
                onSuccess = {
                    controller.popBackStack()
                }
            )
        }

        composable(route = Route.SettingsRoute.route) {
            SettingsScreen(
                onThemeClick = {
                    controller.go(Route.SettingsThemesRoute.withArgs())
                },
                onReminderClick = {
                    controller.go(Route.SettingsReminderRoute.withArgs())
                },
                onAppInfoClick = {
                    controller.go(Route.SettingsAppInfoRoute.withArgs())
                },
                onFeedbackClick = {
                    controller.go(Route.SettingsFeedbackRoute.withArgs())
                },
            )
        }

        composable(
            route = Route.TherapyRoute.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://natai.app/diary/therapy"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            TherapyScreen(
                onClickCreateAccount = {
                    controller.go(Route.RegistrationRoute.withArgs())
                },
            )
        }

        composable(route = Route.AnalyticsRoute.route) {
            AnalyticsScreen(vm = vm)
        }

        composable(route = Route.LoginRoute.route) {
            LoginScreen(
                appViewModel = vm,
                onLoginSuccess = {
                    controller.clearBackStack(Route.LoginRoute.route)
                    controller.go(Route.MainRoute.withArgs())
                },
                onClickCreateAccount = {
                    controller.go(Route.RegistrationRoute.withArgs())
                }
            )
        }

        composable(route = Route.RegistrationRoute.route) {
            RegistrationScreen(
                appViewModel = vm,
                onRegistrationSuccess = {
                    controller.clearBackStack(Route.ManageAccountRoute.withArgs())
                    controller.go(Route.ManageAccountRoute.withArgs())
                },
                onClickLogin = {
                    controller.go(Route.LoginRoute.withArgs())
                },
                onClickTerms = {
                    controller.go(Route.TermsRoute.withArgs())
                }
            )
        }

        composable(route = Route.ManageAccountRoute.route) {
            ManageAccountScreen(
                vm = vm,
                onClickCreateAccount = {
                    controller.go(Route.RegistrationRoute.withArgs())
                },
                onClickDeleteAccount = {
                    controller.go(Route.DeleteAccountRoute.withArgs())
                },
                onClickLogin = {
                    controller.go(Route.LoginRoute.withArgs())
                },
                onLogout = {
                    controller.clearBackStack(Route.ManageAccountRoute.route)
                    controller.go(Route.MainRoute.withArgs())
                }
            )
        }

        composable(route = Route.DeleteAccountRoute.route) {
            DeleteAccountScreen(
                vm = vm,
                onAccountDeleted = {
                    controller.clearBackStack(Route.ManageAccountRoute.route)
                    controller.go(Route.MainRoute.withArgs())
                }
            )
        }

        composable(route = Route.SettingsThemesRoute.route) {
            ThemesScreen(
                vm = vm,
                onThemeChanged = {
                    vm.changeTheme(it)
                }
            )
        }

        composable(route = Route.SettingsReminderRoute.route) {
            ReminderScreen(
                onAskForNotificationPermission = onAskForNotificationPermission,
                onSave = {
                    controller.popBackStack()
                }
            )
        }

        composable(route = Route.SettingsAppInfoRoute.route) {
            AppInfoScreen()
        }

        composable(route = Route.SettingsFeedbackRoute.route) {
            FeedbackScreen()
        }

        composable(route = Route.TermsRoute.route) {
            TermsScreen()
        }
    }
}