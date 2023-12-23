package com.svbackend.natai.android.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.User
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.dto.CityDto
import kotlinx.coroutines.launch

class UpdateProfileViewModel(application: Application) : AndroidViewModel(application) {
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient

    val cities = mutableStateOf(emptyList<CityDto>())
    val selectedCity = mutableStateOf<CityDto?>(null)
    val name = mutableStateOf("")
    val enableEmailNotifications = mutableStateOf(true)

    fun loadCitiesByQuery(query: String) {
        viewModelScope.launch {
            try {
                val response = api.loadCitiesAutocomplete(query)
                cities.value = response.cities
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun onChangeName(name: String) {
        this.name.value = name
    }

    fun onChangeCity(city: CityDto) {
        selectedCity.value = city
    }

    fun onChangeEnableEmailNotifications(enable: Boolean) {
        enableEmailNotifications.value = enable
    }

    fun initData(user: User) {
        name.value = user.name
        selectedCity.value = user.city
        enableEmailNotifications.value = user.enableEmailNotifications
    }

    init {
        viewModelScope.launch {
            try {
                val response = api.loadCities()
                cities.value = response.cities
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}