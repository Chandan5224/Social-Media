package com.example.socialmedia20.Notification


class SharedPrefManager private constructor() {
    private var token: String? = null

    companion object {
        @Volatile
        private var instance: SharedPrefManager? = null

        fun getInstance() = synchronized(this) {
            instance ?: SharedPrefManager().also { instance = it }
        }
    }

    operator fun invoke(token: String) {
        this.token = token
    }

    fun getToken(): String? {
        return token
    }
}