package com.svbackend.natai.android

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.svbackend.natai.android.service.ApiSyncService
import com.svbackend.natai.android.service.ApiSyncWorker
import com.svbackend.natai.android.service.ReminderWorker
import com.svbackend.natai.android.ui.NataiTheme
import com.svbackend.natai.android.ui.UserTheme
import com.svbackend.natai.android.ui.component.DefaultLayout
import com.svbackend.natai.android.utils.go
import com.svbackend.natai.android.utils.hasInternetConnection
import com.svbackend.natai.android.viewmodel.NoteViewModel
import com.svbackend.natai.android.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import java.time.Duration

class MainActivity : ScopedActivity() {

    private lateinit var account: Auth0
    private lateinit var credsManager: CredentialsManager
    private lateinit var authClient: AuthenticationAPIClient
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var apiSyncService: ApiSyncService
    private lateinit var prefs: SharedPreferences

    private val viewModel by viewModels<NoteViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .apply {
                setKeepOnScreenCondition { splashViewModel.isLoading.value }
            }

        super.onCreate(savedInstanceState)

        (application as DiaryApplication).let {
            account = it.appContainer.auth0
            credsManager = it.appContainer.credentialsManager
            authClient = it.appContainer.auth0ApiClient
            connectivityManager = it.appContainer.connectivityManager
            apiSyncService = it.appContainer.apiSyncService
            prefs = it.appContainer.sharedPrefs
        }

        //val prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
        val theme = prefs.getString(getString(R.string.pref_theme_key), null) ?: "Pink"
        val userTheme: UserTheme = UserTheme.strToTheme(theme)

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val controller = rememberNavController()

            NataiTheme(userTheme = userTheme, vm = viewModel) {
                DefaultLayout(
                    vm = viewModel,
                    drawerState = drawerState,
                    scope = scope,
                    toggleDrawer = {
                        scope.launch {
                            if (drawerState.isOpen)
                                drawerState.close()
                            else
                                drawerState.open()
                        }
                    },
                    addNote = {
                        controller.go(Route.NewNoteRoute.withArgs())
                    },
                    onLogin = {
                        onLogin(prefs)
                    },
                    onNavigateTo = {
                        scope.launch {
                            if (drawerState.isOpen) drawerState.close()
                        }
                        controller.go(it)
                    },
                    content = {
                        Navigation(controller, onLoginClick = { onLogin(prefs) }, vm = viewModel)
                    }
                )
            }
        }

        loadCreds(prefs)
    }

    private fun loadCreds(prefs: SharedPreferences) = launch {
        val workManager = WorkManager.getInstance(this@MainActivity)

        credsManager.getCredentials(
            object : Callback<Credentials, CredentialsManagerException> {
                override fun onFailure(error: CredentialsManagerException) {
                    onCredsFailure()
                    splashViewModel.loaded()
                }

                override fun onSuccess(result: Credentials) {
                    onCredsSuccess(prefs = prefs, creds = result)
                    splashViewModel.loaded()

                    val apiSyncWorkRequest: PeriodicWorkRequest =
                        PeriodicWorkRequestBuilder<ApiSyncWorker>(Duration.ofHours(4))
                            .build()

                    workManager.enqueueUniquePeriodicWork(
                        "api_sync_work",
                        ExistingPeriodicWorkPolicy.KEEP,
                        apiSyncWorkRequest
                    )
                }
            })

        val reminderWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(Duration.ofHours(12))
                .build()

        workManager.enqueueUniquePeriodicWork(
            "reminder_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderWorkRequest
        )
    }

    private fun onLogin(prefs: SharedPreferences) {
        WebAuthProvider.login(account)
            .withScheme("natai")
            .withScope("openid profile email offline_access")
            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                    Toast
                        .makeText(
                            this@MainActivity,
                            "Error: ${error.message}",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Credentials) {
                    onCredsSuccess(prefs, result)
                    credsManager.saveCredentials(result)
                }
            })
    }

    private fun onCredsFailure() = launch {
        viewModel.credsFailure()
    }

    private fun onProfileLoaded(profile: UserProfile) = launch {
        viewModel.userProfileLoaded(profile)
    }

    private fun onCredsSuccess(prefs: SharedPreferences, creds: Credentials) = launch {
        loadUserProfile(creds.accessToken)
        with(prefs.edit()) {
            putString("access_token", creds.accessToken)
            putString("id_token", creds.idToken)
            apply()
        }
        viewModel.login()
    }

    private fun syncWithApi() = launch {
        val hasInternet = hasInternetConnection(connectivityManager)
        if (!hasInternet) {
            return@launch
        }

        viewModel.startSync()

        try {
            apiSyncService.syncNotes()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            viewModel.finishSync()
        }
    }

    private fun loadUserProfile(accessToken: String) {
        // With the access token, call `userInfo` and get the profile from Auth0.
        authClient.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                    Toast.makeText(
                        this@MainActivity,
                        "Error getting profile \n${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onSuccess(result: UserProfile) {
                    onProfileLoaded(result)
                    Toast.makeText(
                        this@MainActivity,
                        "Login Successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    syncWithApi()
                }
            })
    }

    // todo call this
    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("natai")
            .start(
                this,
                object : Callback<Void?, AuthenticationException> {
                    override fun onSuccess(result: Void?) {
                        // The user has been logged out!
                        Toast.makeText(
                            this@MainActivity,
                            "Successfully logged out!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(error: AuthenticationException) {
                        Toast.makeText(
                            this@MainActivity,
                            "Couldn't Logout!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }
}