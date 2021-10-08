package com.example.watermarkdialog

import android.graphics.Bitmap
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

object Utils {


    fun saveAsPNG(inputBitmap: Bitmap, filePath: String) {

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(filePath)
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                Log.d("mridx", e.toString())
            }
        }
    }

    fun saveAsJPG(inputBitmap: Bitmap, filePath: String) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(filePath)
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                Log.d("mridx", e.toString())
            }
        }
    }

}