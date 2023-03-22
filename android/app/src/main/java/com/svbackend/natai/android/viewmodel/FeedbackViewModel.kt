package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.request.GeneralFeedbackRequest
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "FeedbackViewModel"
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient
    val prefs = (application as DiaryApplication).appContainer.sharedPrefs

    val isLoading = mutableStateOf(false)
    val showThankYou = mutableStateOf(false)

    val selectedRating = mutableStateOf<Int?>(null)
    val feedbackContent = mutableStateOf("")

    fun sendFeedback() = viewModelScope.launch {
        isLoading.value = true

        val content = feedbackContent.value
        val rating = selectedRating.value

        if (content.isBlank() && rating == null) {
            Toast
                .makeText(getApplication(), "Please enter some feedback", Toast.LENGTH_SHORT)
                .show()
            return@launch
        }

        try {
            api.sendGeneralFeedback(
                GeneralFeedbackRequest(
                    content = content,
                    stars = rating
                )
            )
            Toast
                .makeText(getApplication(), "Thank you for your feedback!", Toast.LENGTH_SHORT)
                .show()
            setFeedbackContent("")
            setSelectedRating(0)

            showThankYou.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send feedback", e)
            Toast
                .makeText(getApplication(), "Failed to send feedback", Toast.LENGTH_SHORT)
                .show()
        } finally {
            isLoading.value = false
        }
    }

    fun setSelectedRating(i: Int) {
        selectedRating.value = i
    }

    fun setFeedbackContent(it: String) {
        feedbackContent.value = it
    }
}