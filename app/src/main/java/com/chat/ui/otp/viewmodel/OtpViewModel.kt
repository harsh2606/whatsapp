package com.chat.ui.otp.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chat.R
import com.chat.apputils.Utils
import com.chat.base.viewmodel.BaseViewModel
import com.chat.databinding.ActivityOtp2Binding
import com.chat.interfaces.TopBarClickListener
import com.chat.ui.profile.view.ProfileActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.mukesh.OnOtpCompletionListener
import java.util.concurrent.TimeUnit

class OtpViewModel (application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityOtp2Binding
    private lateinit var mContext: Context
    var fauth: FirebaseAuth? = null

    var verificationId: String? = null
    var dialog: ProgressDialog? = null


    fun setBinder(binder: ActivityOtp2Binding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Sending OTP...")
        dialog!!.setCancelable(false)
        dialog!!.show()

        fauth = FirebaseAuth.getInstance()

        (mContext as AppCompatActivity).getSupportActionBar()?.hide()

        val phoneNumber: String = (mContext as Activity).getIntent().getStringExtra("phoneNumber")!!

        binder.phoneLbl.setText("Verify $phoneNumber")

        val options = PhoneAuthOptions.newBuilder(fauth!!)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(mContext as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
                override fun onVerificationFailed(e: FirebaseException) {}
                override fun onCodeSent(
                    verifyId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    dialog!!.dismiss()
                    verificationId = verifyId
                    val imm =
                        (mContext).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binder.otpView.requestFocus()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        binder.otpView.setOtpCompletionListener(OnOtpCompletionListener { otp ->
            val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, otp) }
            if (credential != null) {
                fauth!!.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(mContext, ProfileActivity::class.java)
                        mContext.startActivity(intent)
                        (mContext as Activity).finishAffinity()
                    } else {
                        Toast.makeText(mContext, "Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


    }


    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.menu))) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }         }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }



    inner class ViewClickHandler {

        fun onSignOut(view: View) {

        }
    }


}



