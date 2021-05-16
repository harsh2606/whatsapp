package com.chat.ui.otp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.chat.R
import com.chat.base.view.BaseActivity
import com.chat.databinding.ActivityOtp2Binding
import com.chat.ui.otp.viewmodel.OtpViewModel

class OtpActivity : BaseActivity() {
    lateinit var binding: ActivityOtp2Binding
    lateinit var viewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp2)
        viewModel = ViewModelProvider(activity).get(OtpViewModel::class.java)
        viewModel.setBinder(binding)
    }
}