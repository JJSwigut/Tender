package com.jjswigut.data.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.jjswigut.core.utils.Resource
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "getResult: Result is successful $body")
                if (body != null) return Resource.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T> {
        Log.d(TAG, "error: $message")
        return Resource.error("Network call has failed for a following reason: $message")
    }
}