package com.jjswigut.data.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.jjswigut.core.utils.State
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): State<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "getResult: Result is successful $body")
                if (body != null) return State.Success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun error(message: String): State<Nothing> {
        Log.d(TAG, "error: $message")
        return State.Failed("Network call has failed for a following reason: $message")
    }
}