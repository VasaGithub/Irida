package com.travelplanner.irida.domain

data class GalleryImage(
    val id: String,
    val emoji: String,
    val label: String,
    val isTop: Boolean = false
)