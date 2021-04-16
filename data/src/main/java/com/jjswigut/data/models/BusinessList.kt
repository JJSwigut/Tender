package com.jjswigut.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusinessList(
    val businesses: List<Businesses>? = null,
    val region: Region? = null,
    val total: Int? = null
) {
    @JsonClass(generateAdapter = true)
    data class Businesses(
        val alias: String? = null,
        val categories: List<Category>? = null,
        val coordinates: Coordinates? = null,
        @Json(name = "display_phone")
        val displayPhone: String? = null,
        val distance: Double? = null,
        val id: String? = null,
        @Json(name = "image_url")
        val imageUrl: String? = null,
        @Json(name = "is_closed")
        val isClosed: Boolean? = null,
        val location: Location? = null,
        val name: String? = null,
        val phone: String? = null,
        val price: String? = null,
        val rating: Double? = null,
        @Json(name = "review_count")
        val reviewCount: Int? = null,
        val transactions: List<String>? = null,
        val url: String? = null
    ) {
        @JsonClass(generateAdapter = true)
        data class Category(
            val alias: String? = null,
            val title: String? = null
        )

        @JsonClass(generateAdapter = true)
        data class Coordinates(
            val latitude: Double? = null,
            val longitude: Double? = null
        )

        @JsonClass(generateAdapter = true)
        data class Location(
            val address1: String? = null,
            val address2: String? = null,
            val address3: String? = null,
            val city: String? = null,
            val country: String? = null,
            @Json(name = "display_address")
            val displayAddress: List<String>? = null,
            val state: String? = null,
            @Json(name = "zip_code")
            val zipCode: String? = null
        )
    }

    @JsonClass(generateAdapter = true)
    data class Region(
        val center: Center? = null
    ) {
        @JsonClass(generateAdapter = true)
        data class Center(
            val latitude: Double? = null,
            val longitude: Double? = null
        )
    }
}