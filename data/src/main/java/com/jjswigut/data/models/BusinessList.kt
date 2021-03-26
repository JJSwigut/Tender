package com.jjswigut.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusinessList(
    val businesses: List<Businesses>,
    val region: Region,
    val total: Int
) {
    @JsonClass(generateAdapter = true)
    data class Businesses(
        val alias: String,
        val categories: List<Category>,
        val coordinates: Coordinates,
        @Json(name = "display_phone")
        val displayPhone: String,
        val distance: Double,
        val id: String,
        @Json(name = "image_url")
        val imageUrl: String,
        @Json(name = "is_closed")
        val isClosed: Boolean,
        val location: Location,
        val name: String,
        val phone: String,
        val price: String?,
        val rating: Double,
        @Json(name = "review_count")
        val reviewCount: Int,
        val transactions: List<String>,
        val url: String
    ) {
        @JsonClass(generateAdapter = true)
        data class Category(
            val alias: String,
            val title: String
        )

        @JsonClass(generateAdapter = true)
        data class Coordinates(
            val latitude: Double,
            val longitude: Double
        )

        @JsonClass(generateAdapter = true)
        data class Location(
            val address1: String,
            val address2: String?,
            val address3: String?,
            val city: String,
            val country: String,
            @Json(name = "display_address")
            val displayAddress: List<String>,
            val state: String,
            @Json(name = "zip_code")
            val zipCode: String
        )
    }

    @JsonClass(generateAdapter = true)
    data class Region(
        val center: Center
    ) {
        @JsonClass(generateAdapter = true)
        data class Center(
            val latitude: Double,
            val longitude: Double
        )
    }
}