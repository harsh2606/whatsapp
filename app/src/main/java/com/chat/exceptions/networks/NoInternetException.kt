package com.chat.exceptions.networks

import com.chat.apputils.Debug

class NoInternetException : NetworkException() {

    override var errMessage: String = "No Internet Available"

    override var title: String = "Alert"

    override fun printStackTrace() {
        super.printStackTrace()
        Debug.e("NoInternet","No Internet Connection")
    }

    override fun getLocalizedMessage(): String {
        return super.getLocalizedMessage()
    }
}