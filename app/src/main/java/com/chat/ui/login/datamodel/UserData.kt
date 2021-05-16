package com.chat.ui.login.datamodel

import java.io.Serializable


class UserData: Serializable {
    var id: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var socialID: String? = null
    var type: String? = null
    var shopID: String? = null
    var isAdmin: Boolean? = false
    var isApproved: Boolean? = false
    var menu: List<String> = ArrayList<String>()
    var fcm_token: String? = null
    var userProfile: String? = null
    var mobilenumber: String? = null

}