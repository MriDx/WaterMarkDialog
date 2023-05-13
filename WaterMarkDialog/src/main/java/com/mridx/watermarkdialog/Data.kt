/*
 * Copyright (c) 2021.
 * @author MriDx
 */

package com.mridx.watermarkdialog

import android.graphics.Bitmap
import android.graphics.Typeface
import androidx.annotation.ColorInt

object Data {

    enum class WaterMarkPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    data class WaterMarkData(
        var waterMarks: Map<String, String>,
        var position: WaterMarkPosition
    )

    data class WaterMarkDataV2(
        var waterMarks: ArrayList<WaterMark>,
        var position: WaterMarkPosition,
        var logo: WaterMarkImage? = null
    )


    sealed class WaterMark
    data class WaterMarkText(
        var text: String,
        @ColorInt var color: Int,
        var textSize: Float = 0.1f,
        var typeFace: Typeface = Typeface.create("Roboto", Typeface.NORMAL),
    ) : WaterMark()

    data class WaterMarkImage(
        var imageBitmap: Bitmap,
        var height: Float = 80f,
        var width: Float = 80f,
    ) : WaterMark()


}