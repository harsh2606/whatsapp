package com.chat.ui.profile.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chat.R
import com.chat.apputils.Utils
import com.chat.base.viewmodel.BaseViewModel
import com.chat.databinding.ActivityProfile2Binding
import com.chat.interfaces.TopBarClickListener
import com.chat.ui.home.view.HomeActivity
import com.chat.ui.profile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityProfile2Binding
    private lateinit var mContext: Context
    override var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null


    fun setBinder(binder: ActivityProfile2Binding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {


        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        (mContext as AppCompatActivity).supportActionBar?.hide()




    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (data.data != null) {
                binder.imageView.setImageURI(data.data)
                selectedImage = data.data
            }
        }
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

        fun onProfileImage(view: View) {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            (mContext as Activity).startActivityForResult(intent, 45)
        }

        fun onContinue(view: View){
            val name: String = binder.nameBox.getText().toString()

            if (name.isEmpty()) {
                binder.nameBox.setError("Please type a name")
                return
            }

            if (selectedImage != null) {
                val reference = storage!!.reference.child("Profiles").child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name: String = binder.nameBox.getText().toString()
                            val user = phone?.let { User(uid, name, it, imageUrl) }
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnSuccessListener {
                                    dialog!!.dismiss()
                                    val intent = Intent(
                                        mContext,
                                        HomeActivity::class.java
                                    )
                                        mContext.startActivity(intent)
                                    (mContext as Activity).finish()
                                }
                        }
                    }
                }
            }
        }
    }


}


