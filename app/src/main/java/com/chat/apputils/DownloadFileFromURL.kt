package com.chat.apputils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


@Suppress("DEPRECATION")
class DownloadFileFromURL(mContext: Context) : AsyncTask<String, String, String>() {
    var pd: ProgressDialog? = null
    var mContext: Context = mContext

    override fun onPreExecute() {
        super.onPreExecute()
        pd = ProgressDialog(mContext)
        pd!!.setTitle("Processing...")
        pd!!.setMessage("Please wait.")
        pd!!.max = 100
        pd!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd!!.setCancelable(false)
        pd!!.show()
    }

    override fun doInBackground(vararg f_url: String?): String? {
        var count: Int = 0
        var destination: File? = null
        try {
            var dir = mContext.filesDir
            val futureStudioIconFile = File(dir, "AmazingStar")
            if (!futureStudioIconFile.exists()) {
                futureStudioIconFile.mkdirs()
            }
            destination = File(dir, "amazingstar.apk")

            val url = URL(f_url[0])
            val connection: URLConnection = url.openConnection()
            connection.connect()

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            val lengthOfFile: Int = connection.contentLength

            // download the file
            val input: InputStream = BufferedInputStream(url.openStream())
            val output = FileOutputStream(destination)
            val data = ByteArray(1024) //anybody know what 1024 means ?
            var total: Long = 0
            while (input.read(data).also({ count = it }) != -1) {
                total += count.toLong()
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (total * 100 / lengthOfFile).toInt())

                // writing data to file
                output.write(data, 0, count)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()

        } catch (e: Exception) {
            Debug.e("Error: ", e.message)
        }
        return destination?.absolutePath
    }

    override fun onProgressUpdate(vararg progress: String) {
        // setting progress percentage
        pd!!.progress = progress[0].toInt()
    }

    override fun onPostExecute(file_url: String?) {
        if (pd != null) {
            pd!!.dismiss()
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                getUriFromFile(file_url!!),
                "application/vnd.android.package-archive"
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mContext.startActivity(intent)
            (mContext as Activity).finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUriFromFile(location: String): Uri? {
        return if (Build.VERSION.SDK_INT < 24) {
            Uri.fromFile(File(location))
        } else {
            FileProvider.getUriForFile(
                mContext,
                mContext.applicationContext.packageName + ".provider",
                File(location)
            )
        }
    }
}