package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.UserTheme
import kotlinx.coroutines.launch

class ThemesViewModel(application: Application) : AndroidViewModel(application) {
    private val app: DiaryApplication = (application as DiaryApplication)
    private val prefs: SharedPreferences = (application as DiaryApplication).appContainer.sharedPrefs
    fun changeTheme(newTheme: UserTheme) {
        viewModelScope.launch {
            prefs.edit()
                .putString(app.getString(R.string.pref_theme_key), newTheme.name)
                .apply()
        }
    }
}