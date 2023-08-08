package com.example.socialmedia20.Notification

import android.app.Activity
import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class FCMNotificationSender(
    var userFcmToken: String,
    var title: String,
    var body: String,
    var imageUrl: String,
    var userImageUrl: String,
    var mActivity: Activity
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAAFivfauA:APA91bHCjnFQYpPooqkhdjkrFEOClmxnz55SyDq4nz-A7ViwUe7iIXLAso-SFEGw7sxvKA9Dqufhgt7DWk-4sTaomy_8Go3gU4ttZ2FudpUkmJlPCpu3o7kc76gZ9OYCtwBet4ZjMCTk"

    fun sendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            mainObj.put("notification", notiObject)
            val data = JSONObject()
            data.put("key", SharedPrefManager.getInstance().getToken())
            data.put("largeImage", imageUrl)
            data.put("userImage", userImageUrl)
            mainObj.put("data", data)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                postUrl,
                mainObj,
                Response.Listener<JSONObject?> {
                    // code run is got response
                },
                Response.ErrorListener {
                    // code run is got error
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "key=$fcmServerKey"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}

