package com.turktalk.apputils

import android.content.Context
import com.chat.apputils.Debug
import com.chat.network.APIClient
import com.chat.network.RetrofitUtils
import okhttp3.Call
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Created by Kartum Infotech (Bhavesh Hirpara) on 8/24/2020.
 */
object PushNotification {
    fun sendNotifications(
        receivers: MutableList<String>,
        nType: String,
        msg: String,
        title: String,
        sender: String,
        context: Context
    ) {


        val jsonArray = JSONArray()
        for (item in receivers) {
            jsonArray.put(item)
        }
        val registrationIds = jsonArray.toString()
        Debug.e("registration_ids", registrationIds)
        val dataJson = JSONObject()
        dataJson.put("title", title)
        dataJson.put("message", msg)
        dataJson.put("sender", sender)
        dataJson.put("push_type", nType)

        val jsonObject = JSONObject()
        jsonObject.put("registration_ids", jsonArray)
        jsonObject.put("data", dataJson)
        val call = APIClient.newRequestPostJson(
            context,
            "https://fcm.googleapis.com/fcm/send",
            jsonObject.toString()
        )
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val str = RetrofitUtils.getResponseString(response.body)
                Debug.e("sendNotifications", str)
            }

        })
    }

}