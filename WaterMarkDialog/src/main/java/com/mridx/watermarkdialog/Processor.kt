/*
 * Copyright (c) 2021.
 * @author MriDx
 */

package com.mridx.watermarkdialog

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.File

object Processor {

    private fun File.toBitmap(maxHeight: Float, maxWidth: Float): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        var bmp = BitmapFactory.decodeFile(this.path, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        var maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }
        options.apply {
            inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            inJustDecodeBounds = false
            inDither = false
            inPurgeable = true
            inInputShareable = true
            inTempStorage = ByteArray(16 * 1024)
        }

        try {
            bmp = BitmapFactory.decodeFile(this.path, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }
        return bmp
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun process(
        file: File,
        maxHeight: Float = 1920.0f,
        maxWidth: Float = 1920.0f,
        waterMarkData: Data.WaterMarkData,
        typeface: Typeface,
    ): Bitmap? {
        val bitmap = file.toBitmap(maxHeight = maxHeight, maxWidth = maxWidth)
            ?: throw Exception("Out of memory")

        var bmp = bitmap.copy(bitmap.config, true)
        /* var actualHeight = bmp.height
         var actualWidth = bmp.width*/

        val processedBmp =
            BitmapUtils.addTags(bmp = bmp, waterMarkData = waterMarkData, textSizeRatio = 0.1f, typeFace = typeface)

        return processedBmp

    }

    fun process(
        view: ImageView,
        maxHeight: Float = 1920.0f,
        maxWidth: Float = 1920.0f,
        waterMarkData: Data.WaterMarkData,
        typeface: Typeface,
    ): Bitmap? {
        val bitmap = view.toBitmap()
        val bmp = bitmap.copy(bitmap.config, true)
        val processedBmp =
            BitmapUtils.addTags(bmp = bmp, waterMarkData = waterMarkData, textSizeRatio = 0.1f, typeFace = typeface)
                ?: throw Exception("Bitmap can not be null !")

        return compressBitmap(bitmap = processedBmp, maxHeight = maxHeight, maxWidth = maxWidth)
    }

    fun process(
        view: ImageView,
        maxWidth: Float = 1920.0f,
        maxHeight: Float = 1920.0f,
        waterMarkData: Data.WaterMarkDataV2
    ): Bitmap {
        val bitmap = view.toBitmap()
        val bmp = bitmap.copy(bitmap.config, true)
        val processedBitmap = BitmapUtils.addTags(bmp = bmp, waterMarkData = waterMarkData)
        return processedBitmap
    }

    private fun ImageView.toBitmap(): Bitmap {
        if (this.drawable == null) throw Exception("Provided ImageView does not have drawable bitmap")
        return (this.drawable as BitmapDrawable).bitmap
    }

    private fun compressBitmap(bitmap: Bitmap, maxHeight: Float, maxWidth: Float): Bitmap? {

        var scaledBitmap: Bitmap?
        var bmp = bitmap.copy(bitmap.config, true)


        var actualHeight = /*options.outHeight*/ bmp.height
        var actualWidth = /*options.outWidth*/ bmp.width

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val ratioX = actualWidth / bmp.width.toFloat()
        val ratioY = actualHeight / bmp.height.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        bmp.recycle()

        return scaledBitmap
    }

}