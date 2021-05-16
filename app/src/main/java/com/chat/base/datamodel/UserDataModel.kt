package com.chat.base.datamodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.chat.datasource.UserRepository
import com.chat.network.APIClient
import com.chat.network.APIinterface

class UserDataModel {
    fun getArea(context: Context): MutableLiveData<AllArea> {
        val apInterface: APIinterface =
            APIClient.newRequestRetrofit(context).create(APIinterface::class.java)
        val userRepository = UserRepository(apInterface)
        return userRepository.getArea("")
    }
}