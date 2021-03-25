package com.jjswigut.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BusinessList(
    @Json(name = "businesses")
    val businesses: List<Businesses>
) {
    @JsonClass(generateAdapter = true)
    data class Businesses(
        @Json(name = "alias")
        val alias: String,
        @Json(name = "categories")
        val categories: List<Category>,
        @Json(name = "coordinates")
        val coordinates: Coordinates,
        @Json(name = "display_phone")
        val displayPhone: String,
        @Json(name = "distance")
        val distance: Double,
        @Json(name = "id")
        val id: String,
        @Json(name = "image_url")
        val imageUrl: String,
        @Json(name = "is_closed")
        val isClosed: Boolean,
        @Json(name = "location")
        val location: Location,
        @Json(name = "name")
        val name: String,
        @Json(name = "phone")
        val phone: String,
        @Json(name = "price")
        val price: String,
        @Json(name = "rating")
        val rating: Double,
        @Json(name = "review_count")
        val reviewCount: Int,
        @Json(name = "transactions")
        val transactions: List<String>,
        @Json(name = "url")
        val url: String
    ) {
        @JsonClass(generateAdapter = true)
        data class Category(
            @Json(name = "alias")
            val alias: String,
            @Json(name = "title")
            val title: String
        )

        @JsonClass(generateAdapter = true)
        data class Coordinates(
            @Json(name = "latitude")
            val latitude: Double,
            @Json(name = "longitude")
            val longitude: Double
        )

        @JsonClass(generateAdapter = true)
        data class Location(
            @Json(name = "address1")
            val address1: String,
            @Json(name = "address2")
            val address2: String,
            @Json(name = "address3")
            val address3: String,
            @Json(name = "city")
            val city: String,
            @Json(name = "country")
            val country: String,
            @Json(name = "display_address")
            val displayAddress: List<String>,
            @Json(name = "state")
            val state: String,
            @Json(name = "zip_code")
            val zipCode: String
        )
    }
}