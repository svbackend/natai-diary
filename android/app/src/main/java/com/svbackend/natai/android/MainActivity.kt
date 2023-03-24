package com.svbackend.natai.android

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.svbackend.natai.android.service.ApiSyncService
import com.svbackend.natai.android.service.ApiSyncWorker
import com.svbackend.natai.android.service.ReminderWorker
import com.svbackend.natai.android.ui.NataiTheme
import com.svbackend.natai.android.ui.UserTheme
import com.svbackend.natai.android.ui.component.DefaultLayout
import com.svbackend.natai.android.utils.getUserCloudId
import com.svbackend.natai.android.utils.go
import com.svbackend.natai.android.utils.hasInternetConnection
import com.svbackend.natai.android.viewmodel.NoteViewModel
import com.svbackend.natai.android.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import java.time.Duration

class MainActivity : ScopedActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var apiSyncService: ApiSyncService
    private lateinit var prefs: SharedPreferences

    private val viewModel by viewModels<NoteViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()

    private val pushNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .apply {
                setKeepOnScreenCondition { splashViewModel.isLoading.value }
            }

        super.onCreate(savedInstanceState)

        (application as DiaryApplication).let {
            connectivityManager = it.appContainer.connectivityManager
            apiSyncService = it.appContainer.apiSyncService
            prefs = it.appContainer.sharedPrefs
        }

        //val prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
        val theme = prefs.getString(getString(R.string.pref_theme_key), null) ?: "Default"
        val userTheme: UserTheme = UserTheme.strToTheme(theme)

        setContent {
            val controller = rememberNavController()

            NataiTheme(userTheme = userTheme, vm = viewModel) {
                DefaultLayout(
                    vm = viewModel,
                    onNavigateTo = {
                        controller.go(it)
                    },
                    addNote = {
                        controller.go(Route.NewNoteRoute.withArgs())
                    },
                    content = {
                        Navigation(controller, vm = viewModel, onAskForNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        })
                    }
                )
            }
        }

        loadCreds()
    }

    private fun loadCreds() = launch {
        val workManager = WorkManager.getInstance(this@MainActivity)

        val reminderWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(Duration.ofHours(12))
                .build()

        workManager.enqueueUniquePeriodicWork(
            "reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderWorkRequest
        )

        prefs.getUserCloudId()?.let { cloudId ->
            viewModel.setUserCloudId(cloudId)

            syncWithApi()
        }

        splashViewModel.loaded()
    }

    private fun syncWithApi() {
        val hasInternet = hasInternetConnection(connectivityManager)

        if (!hasInternet) {
            return
        }

        try {
            viewModel.sync()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}