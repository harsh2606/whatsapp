package com.chat.ui.phonenumber.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.chat.R
import com.chat.base.view.BaseActivity
import com.chat.databinding.ActivityPhoneNumber2Binding
import com.chat.ui.phonenumber.viewmodel.PhoneNumberViewModel

class PhoneNumberActivity : BaseActivity() {
    lateinit var binding: ActivityPhoneNumber2Binding
    lateinit var viewModel: PhoneNumberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_number2)
        viewModel = ViewModelProvider(activity).get(PhoneNumberViewModel::class.java)
        viewModel.setBinder(binding)
    }
}