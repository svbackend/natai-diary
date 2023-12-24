package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.icu.util.TimeZone
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.dto.CityDto
import com.svbackend.natai.android.http.dto.CloudUserDto
import com.svbackend.natai.android.http.request.UpdateProfileRequest
import com.svbackend.natai.android.utils.throttleLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateProfileViewModel(application: Application) : AndroidViewModel(application) {
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val cities = mutableStateOf(emptyList<CityDto>())
    val selectedCity = mutableStateOf<Int?>(null)
    val name = mutableStateOf(TextFieldValue(""))
    val enableEmailNotifications = mutableStateOf(true)

    val showSuccess = mutableStateOf(false)
    val showError = mutableStateOf(false)

    val isUserLoading = mutableStateOf(true)
    val isCitiesLoading = mutableStateOf(true)
    val isLoadingAutocomplete = mutableStateOf(false)

    val isExpanded = mutableStateOf(false)
    val isSaving = mutableStateOf(false)

    val cityInput = mutableStateOf(TextFieldValue(""))

    val loadCitiesByQueryThrottle = throttleLatest<String>(
        coroutineScope = viewModelScope,
    ) {
        loadCitiesByQuery(it)
    }

    fun onCityInputChange(query: TextFieldValue) {
        cityInput.value = query
        loadCitiesByQueryThrottle(query.text)
    }

    fun loadCitiesByQuery(query: String) {
        isLoadingAutocomplete.value = true
        viewModelScope.launch {
            try {
                val response = api.loadCitiesAutocomplete(query)
                cities.value = response.cities
                isExpanded.value = true
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                isLoadingAutocomplete.value = false
            }
        }
    }

    fun updateProfile() {
        if (name.value.text.isEmpty() || selectedCity.value == null) {
            Toast.makeText(getApplication(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        showError.value = false
        showSuccess.value = false
        isSaving.value = true

        viewModelScope.launch {
            try {
                val response = api.updateProfile(
                    UpdateProfileRequest(
                        name = name.value.text,
                        cityId = selectedCity.value!!,
                        enableEmailNotifications = enableEmailNotifications.value,
                        timezoneOffset = getTimezoneOffset()
                    )
                )

                showSuccess.value = true
            } catch (e: Throwable) {
                e.printStackTrace()
                showError.value = true
            } finally {
                isSaving.value = false
            }

            delay(2500)
            showSuccess.value = false
            showError.value = false
        }
    }

    fun onChangeName(name: TextFieldValue) {
        this.name.value = name
    }

    fun onChangeCity(cityId: Int) {
        selectedCity.value = cityId
        isExpanded.value = false
        val city = cities.value.find { it.id == cityId }
        cityInput.value = TextFieldValue(city?.name ?: "")
    }

    fun onChangeEnableEmailNotifications() {
        enableEmailNotifications.value = enableEmailNotifications.value.not()
    }

    fun initData(user: CloudUserDto) {
        name.value = TextFieldValue(user.name)
        selectedCity.value = user.profile?.city?.id
        cityInput.value = TextFieldValue(user.profile?.city?.name ?: "")
        enableEmailNotifications.value = if (user.profile?.enableEmailNotifications != null) {
            user.profile.enableEmailNotifications
        } else {
            true
        }
    }

    private fun getTimezoneOffset(): Int {
        val timeZone = TimeZone.getDefault()
        return timeZone.rawOffset / (1000 * 60) // in minutes
    }

    fun isLoading(): Boolean {
        return isUserLoading.value || isCitiesLoading.value
    }

    private fun loadInitialData() {
        isCitiesLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.loadCities()
                cities.value = response.cities
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                isCitiesLoading.value = false
            }
        }

        isUserLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.getCurrentUser()
                initData(response.user)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                isUserLoading.value = false
            }
        }
    }

    fun onToggleListVisibility() {
        isExpanded.value = isExpanded.value.not()
    }

    init {
        loadInitialData()
    }
}