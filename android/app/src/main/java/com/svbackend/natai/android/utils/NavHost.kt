package com.svbackend.natai.android.utils

import androidx.navigation.NavHostController

/**
 * @Important Works only with argument-less routes
 */
fun NavHostController.go(destinationRoute: String) {
    val currentRoute = currentBackStackEntry?.destination?.route
    if (currentRoute != destinationRoute) {
        navigate(destinationRoute)
    }
}