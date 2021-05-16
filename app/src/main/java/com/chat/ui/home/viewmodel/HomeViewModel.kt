package com.chat.ui.home.viewmodel


import android.app.*
import android.content.Context
import android.view.View
import com.chat.R
import com.chat.apputils.Utils
import com.chat.base.viewmodel.BaseViewModel
import com.chat.databinding.ActivityHomeBinding
import com.chat.interfaces.TopBarClickListener

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityHomeBinding
    private lateinit var mContext: Context

    fun setBinder(binder: ActivityHomeBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        this.binder.topbar.topBarClickListener = SlideMenuClickListener()
        this.binder.topbar.isTextShow = true
        this.binder.topbar.isNavShow = true
        this.binder.topbar.isBackShow = false
        this.binder.topbar.tvTitleText.text = (mContext as Activity).getString(R.string.home)
        init()
    }

    private fun init() {
      }






    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.menu))) {
                try {
                    onTopMenuClick()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }


    inner class ViewClickHandler {


    }


}



