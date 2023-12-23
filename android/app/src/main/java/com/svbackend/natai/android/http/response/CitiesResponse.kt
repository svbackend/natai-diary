package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.dto.CityDto

data class CitiesResponse(
    val cities: List<CityDto>
)
