package com.chat.base.viewmodel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.chat.R
import com.chat.apputils.*
import com.chat.base.view.BaseActivity
import com.chat.databinding.CustomSideMenuBinding
import com.chat.databinding.DialogPicChooserBinding
import com.chat.ui.MyApplication
import com.chat.ui.login.datamodel.UserData
import com.chat.ui.login.view.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mikepenz.materialdrawer.BuildConfig
import com.mikepenz.materialdrawer.Drawer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


open class BaseViewModel(application: Application) : AppViewModel(application) {
    lateinit var result: Drawer
    private lateinit var activity: Activity
    lateinit var customSideMenuBinding: CustomSideMenuBinding


    val db: FirebaseFirestore?
        get() {
            return (getApplication() as MyApplication).db
        }

    open val auth: FirebaseAuth?
        get() {
            return (getApplication() as MyApplication).auth
        }

    val storageRef: StorageReference?
        get() {
            return (getApplication() as MyApplication).storageRef
        }

    val firebaseStorage: FirebaseStorage?
        get() {
            return (getApplication() as MyApplication).firebaseStorage
        }

    fun finishActivity(mContext: Context) {

//        if (mContext is HomeActivity) {
//
//        } else
        (mContext as Activity).finish()

    }

    fun dismissAlert(alertDialog: AlertDialog?) {
        alertDialog?.dismiss()
    }





//

    fun hideMenu(b: Boolean) {
        try {
            result.closeDrawer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onTopMenuClick() {
        toggleDrawer()
    }

    private fun toggleDrawer() {
        if (result.isDrawerOpen) {
            result.closeDrawer()
        } else {
            result.openDrawer()
        }
    }

    private fun setupFullHeight(
        v: View,
        dialogInterface: DialogInterface,
        linearLayout: LinearLayout,
        mContext: Context
    ) {

        val bottomSheetBehavior = BottomSheetBehavior.from((v.getParent()) as View)
        val layoutParams = linearLayout.layoutParams
        val windowHeight = getWindowHeight(mContext)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        linearLayout.layoutParams = layoutParams
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(mContext: Context): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


//    fun checkPermission(activity: Activity, permissionsListener: PermissionListener) {
//        Dexter.withActivity(activity)
//            .withPermissions(
//                android.Manifest.permission.INTERNET,
//                android.Manifest.permission.READ_SMS
//            ).withListener(object : MultiplePermissionsListener {
//
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
//                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)
//
//                    if (report.areAllPermissionsGranted()) {
//                        if (permissionsListener != null)
//                            permissionsListener.onGranted()
//                    } else {
//                        if (permissionsListener != null)
//                            permissionsListener.onDenied()
////                            showAlertDialog(permissionsListener)
//                    }
//
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    Debug.e("onPermissionRationale", "" + permissions.size)
//                    token.continuePermissionRequest()
//                }
//
//
//            }).check()
//    }

    fun checkPermissionStorageAndCamera(
        activity: Activity,
        permissionsListener: PermissionListener
    ) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)

                    if (report.areAllPermissionsGranted()) {
                        permissionsListener.onGranted()
                    } else {
                        permissionsListener.onDenied()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Debug.e("onPermissionRationale", "" + permissions.size)
                    token.continuePermissionRequest()
                }


            }).check()
        requestAllFilesAccessPermission(activity)
    }

    val ALL_FILES_ACCESS_PERMISSION = 4

    private fun requestAllFilesAccessPermission(mContext: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
//            Toast.makeText(
//                mContext,
//                "We can access all files on external storage now",
//                Toast.LENGTH_SHORT
//            ).show()
        } else {
            val builder = AlertDialog.Builder(mContext)
                .setTitle("Tip")
                .setMessage("We need permission to access all files on external storage")
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    (mContext as Activity).startActivityForResult(
                        intent,
                        ALL_FILES_ACCESS_PERMISSION
                    )
                }
                .setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    fun parseDate(time: String?, output: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = output
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {


            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }











    }







    interface PermissionListener {
        fun onGranted()
        fun onDenied()
    }

//    fun checkPermissionStorageAndCamera(
//        activity: Activity,
//        permissionsListener: PermissionListener
//    ) {
//        Dexter.withActivity(activity)
//            .withPermissions(
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ).withListener(object : MultiplePermissionsListener {
//
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
//                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)
//
//                    if (report.areAllPermissionsGranted()) {
//                        permissionsListener.onGranted()
//                    } else {
//                        permissionsListener.onDenied()
//                    }
//
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    Debug.e("onPermissionRationale", "" + permissions.size)
//                    token.continuePermissionRequest()
//                }
//
//
//            }).check()
//    }


    fun showPermissionAlert(mContext: Context) {
        var dialog: androidx.appcompat.app.AlertDialog? = null
        val builder = androidx.appcompat.app.AlertDialog.Builder(
            mContext as BaseActivity,
            R.style.MyAlertDialogStyle
        )
        builder.setTitle(mContext.getString(R.string.need_permission_title))
        builder.setCancelable(false)
        builder.setMessage(mContext.getString(R.string.err_need_permission_msg))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            dialog.dismiss()
            mContext.startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        }
        builder.setNeutralButton(R.string.btn_cancel) { dialog, which ->
            dialog.dismiss()
        }
        dialog = builder.create()
        dialog!!.show()

    }

    var fileUri: File? = null
    val REQ_CAPTURE_IMAGE = 4470
    val REQ_PICK_IMAGE = 4569
    var type = ""


    fun showPictureChooser(mContext: Context) {
        val pd: AlertDialog.Builder =
            AlertDialog.Builder((mContext as Activity), R.style.MyAlertDialogStyle)
        val binding = DataBindingUtil.inflate<DialogPicChooserBinding>(
            LayoutInflater.from(mContext),
            R.layout.dialog_pic_chooser, null, false
        )

        pd.setView(binding.root)
        val dialog = pd.create()
        binding.tvChooseGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                (mContext as Activity).startActivityForResult(
                    Intent.createChooser(
                        intent,
                        mContext.getString(R.string.err_select_image)
                    ), REQ_PICK_IMAGE
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            dialog.dismiss()
        }

        binding.tvChooseCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true)
            fileUri = File(Utils.getOutputMedia(mContext)!!.absolutePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getUriForShare(mContext, fileUri!!))
            try {
                (mContext as Activity).startActivityForResult(
                    Intent.createChooser(
                        intent,
                        mContext!!.getString(R.string.err_select_image)
                    ), REQ_CAPTURE_IMAGE
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            dialog.dismiss()
        }

        dialog.show()

    }

    interface DateListener {
        fun onDateListener(start: Date, end: Date)
    }

    @ServerTimestamp
    var start: Date? = null

    @ServerTimestamp
    var end: Date? = null

    fun getFirstLastDayOfYear(year: String, onDateListener: DateListener) {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year.toInt()
        cal[Calendar.DAY_OF_YEAR] = 1
        start = cal.time


        cal[Calendar.YEAR] = year.toInt()
        cal[Calendar.MONTH] = 11 // 11 = december

        cal[Calendar.DAY_OF_MONTH] = 31 // new years eve


        end = cal.time

        //Iterate through the two dates

        //Iterate through the two dates
        val gcal = GregorianCalendar()
        gcal.time = start
        while (gcal.time.before(end)) {
            gcal.add(Calendar.DAY_OF_YEAR, 1)
            //Do Something ...
        }
        onDateListener.onDateListener(start!!, end!!)
    }

    fun scrollingWhileKeyboard(layout: ViewGroup, btnBottom: Button) {
        layout.viewTreeObserver
            .addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
                val r = Rect()
                try {
                    layout.getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = layout.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    if (keypadHeight > screenHeight * 0.15) {
                        btnBottom.visibility = View.GONE
                    } else {
                        btnBottom.visibility = View.VISIBLE
                    }

                } catch (e: NullPointerException) {
                }
            })
    }







