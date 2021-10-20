package com.everymomentholy.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL



class DownloadFile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
      /*  var contextWrapper = ContextWrapper(getApplicationContext());
        var directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);
        var file =  File(directory,"");
        var data = "TEST DATA"
        var fos : FileOutputStream = FileOutputStream("fileName", true); // save

        fos.write=data.getBytes()
        fos.close();*/

    }

}
/*

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
private class DownloadFile : AsyncTask<String?, Void?, Bitmap?>() {
    override fun onPreExecute() {
        super.onPreExecute()
    }


    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            val dir = File(mContext.getFilesDir(), "MyImages")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val destination = File(dir, "image.jpg")
            try {
                destination.createNewFile()
                val bos = ByteArrayOutputStream()
                result.compress(Bitmap.CompressFormat.PNG, 0 */
/*ignored for PNG*//*
, bos)
                val bitmapdata: ByteArray = bos.toByteArray()
                val fos = FileOutputStream(destination)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
                selectedFile = destination
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun doInBackground(vararg params: String?): Bitmap? {
        val imageURL = URL[0]
        var bitmap: Bitmap? = null
        try {
            // Download Image from URL
            val input: InputStream = URL(URL).openStream()
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}*/
