package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.dto.CloudSuggestionDto
import com.svbackend.natai.android.http.dto.SuggestionPeriodDto
import com.svbackend.natai.android.http.request.SuggestionFeedbackRequest
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.utils.isLoggedIn
import kotlinx.coroutines.launch

class TherapyViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "TherapyViewModel"
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient
    val prefs = (application as DiaryApplication).appContainer.sharedPrefs

    val isLoading = mutableStateOf(false)
    val suggestions = mutableStateOf<List<CloudSuggestionDto>>(emptyList())
    val selectedSuggestion = mutableStateOf<CloudSuggestionDto?>(null)

    init {
        if (prefs.isLoggedIn()) {
            viewModelScope.launch {
                isLoading.value = true
                val response = api.getSuggestions()
                suggestions.value = response.suggestions
                isLoading.value = false
            }
        }
    }

    fun selectSuggestion(suggestion: CloudSuggestionDto?) {
        selectedSuggestion.value = suggestion
    }

    fun sendFeedbackAndClose(suggestion: CloudSuggestionDto, rating: Int) = viewModelScope.launch {
        try {
            api.sendSuggestionFeedback(
                suggestion.id,
                SuggestionFeedbackRequest(rating)
            )
            suggestions.value = suggestions.value.map {
                if (it.id == suggestion.id) {
                    it.copy(isReceived = true, feedbackRating = rating)
                } else {
                    it
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send feedback", e)
        } finally {
            selectSuggestion(null)
        }
    }

    fun preview(content: String): String {
        val first20Words = content.split(" ").take(20).joinToString(" ")

        return if (first20Words.length < content.length) {
            "$first20Words..."
        } else {
            first20Words
        }
    }

    fun periodToString(period: SuggestionPeriodDto): String {
        val from = LocalDateTimeFormatter.fullDateAmerica.format(period.from)
        val to = LocalDateTimeFormatter.fullDateAmerica.format(period.to)

        return "$from - $to"
    }
}