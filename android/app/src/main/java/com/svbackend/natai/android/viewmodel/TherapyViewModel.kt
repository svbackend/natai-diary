package com.svbackend.natai.android.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.svbackend.natai.android.BuildConfig
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.dto.CloudSuggestionDto
import com.svbackend.natai.android.http.dto.CloudSuggestionLinkDto
import com.svbackend.natai.android.http.dto.SuggestionPeriodDto
import com.svbackend.natai.android.http.exception.FeatureNotAvailableException
import com.svbackend.natai.android.http.request.SuggestionFeedbackRequest
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.utils.isLoggedIn
import kotlinx.coroutines.launch

const val FEATURE_NOT_AVAILABLE = "FEATURE_NOT_AVAILABLE"

class TherapyViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "TherapyViewModel"
    val api: ApiClient = (application as DiaryApplication).appContainer.apiClient
    val prefs = (application as DiaryApplication).appContainer.sharedPrefs
    val paymentSheet = (application as DiaryApplication).appContainer.paymentSheet

    val context = application

    val isLoading = mutableStateOf(false)
    val suggestions = mutableStateOf<List<CloudSuggestionDto>>(emptyList())
    val selectedSuggestion = mutableStateOf<CloudSuggestionDto?>(null)

    val isUserLoggedIn = prefs.isLoggedIn()

    val suggestionLinks = mutableStateOf<List<CloudSuggestionLinkDto>>(emptyList())
    val suggestionLinksLoading = mutableStateOf(false)
    val suggestionLinksError = mutableStateOf<String?>(null)

    val getAccessLoading = mutableStateOf(false)
    val paymentSheetResult = mutableStateOf<PaymentSheetResult?>(null)

    init {
        if (!prefs.isLoggedIn()) {
            loadSuggestions()

            (application as DiaryApplication).appContainer.paymentSheetCallback = {
                when(it) {
                    is PaymentSheetResult.Canceled -> {
                        Log.i(TAG, "Payment Sheet Canceled")
                    }
                    is PaymentSheetResult.Failed -> {
                        paymentSheetResult.value = it
                    }
                    is PaymentSheetResult.Completed -> {
                        loadSuggestions()
                        paymentSheetResult.value = it
                    }
                }
            }
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            isLoading.value = true
            val response = api.getSuggestions()
            suggestions.value = response.suggestions
            isLoading.value = false
        }
    }

    fun selectSuggestion(suggestion: CloudSuggestionDto?) {
        selectedSuggestion.value = suggestion

        if (suggestion != null) {
            viewModelScope.launch {
                suggestionLinksLoading.value = true
                suggestionLinksError.value = null
                try {
                    val response = api.getSuggestionLinks(suggestion.id)
                    suggestionLinks.value = response.links
                    Log.v(TAG, "Loaded ${response.links.size} links")
                    Log.v(TAG, response.links.toString())
                } catch (e: FeatureNotAvailableException) {
                    suggestionLinksError.value = FEATURE_NOT_AVAILABLE
                } catch (e: Throwable) {
                    suggestionLinksError.value = "Failed to load links"
                } finally {
                    suggestionLinksLoading.value = false
                }
            }
        }
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

    fun onClickGetAccess() = viewModelScope.launch {
        if (paymentSheet == null) {
            Log.e(TAG, "PaymentSheet is not ready! (null)")
            return@launch
        }

        getAccessLoading.value = true

        try {
            val response = api.buySuggestionLinks()

            if (response.ephemeralKey == null) {
                Log.e(TAG, "ephemeralKey is null")
                return@launch
            }

            val customerConfig = PaymentSheet.CustomerConfiguration(
                response.customerId,
                response.ephemeralKey
            )
            val publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
            PaymentConfiguration.init(context, publishableKey)

            presentPaymentSheet(
                paymentIntentClientSecret = response.paymentIntentSecret,
                customerConfig = customerConfig,
            )
        } finally {
            getAccessLoading.value = false
        }
    }

    fun presentPaymentSheet(
        paymentIntentClientSecret: String,
        customerConfig: PaymentSheet.CustomerConfiguration
    ) {
        paymentSheet?.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Natai Diary",
                customer = customerConfig,
            )
        )
    }

    fun resetPaymentSheetResult() {
        paymentSheetResult.value = null
    }
}