package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.dto.CloudSuggestionDto

data class SuggestionsResponse(
    val suggestions: List<CloudSuggestionDto>,
)
