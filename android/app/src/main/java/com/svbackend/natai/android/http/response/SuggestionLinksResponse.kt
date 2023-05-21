package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.dto.CloudSuggestionLinkDto

data class SuggestionLinksResponse(
    val links: List<CloudSuggestionLinkDto>,
)

