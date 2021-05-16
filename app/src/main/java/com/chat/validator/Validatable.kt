package com.chat.validator

interface Validatable {

    var value: String?
    var msg: String?

    fun isValid() : Boolean
}