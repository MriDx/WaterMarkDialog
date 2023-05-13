package com.mridx.watermarkdialog

import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log

object Utils {

    /**
     * Calculate width from given value
     * @return Float
     */
    fun calculateDialogWidthFromBitmap(bitmapWidth: Int): Float {
        return when {
            bitmapWidth > 2200 -> {
                bitmapWidth * 0.1f
            }

            bitmapWidth > 1920 -> {
                bitmapWidth * 0.15f
            }

            bitmapWidth > 1600 -> {
                bitmapWidth * 0.2f
            }

            bitmapWidth > 1366 -> {
                bitmapWidth * 0.25f
            }

            bitmapWidth > 1080 -> {
                bitmapWidth * 0.3f
            }

            bitmapWidth > 720 -> {
                bitmapWidth * 0.35f
            }

            else -> {
                bitmapWidth * 0.4f
            }
        }
    }

    /**
     * calculate dialog width to be print
     * @param waterMarks ArrayList of WaterMark
     * @see Data.WaterMark
     *
     * @return Float calculated amount as float
     *
     */
    fun calculateDialogWidthByMeasuringText(
        waterMarks: ArrayList<Data.WaterMark>
    ): Float {
        var dw = 0f
        waterMarks.forEach {
            val wmt = it
            if (wmt is Data.WaterMarkText) {
                val measured =
                    measureText(
                        text = wmt.text,
                        txtSize = wmt.textSize,
                        typeFace = wmt.typeFace
                    )
                if (measured > dw) dw = measured
            }
        }
        return dw
    }

    /**
     * Measures a text width
     * @param text String to be measured
     * @param txtSize Float
     * @param typeFace Typeface
     *
     * @return Float value of the given text to be occupied horizontally
     */
    private fun measureText(text: String, txtSize: Float, typeFace: Typeface): Float {
        return Paint().apply {
            textSize = txtSize
            typeface = typeFace
        }.measureText(text)
    }


    /**
     * calculates dialog height
     * by measuring every lines text size
     * and adding gaps in between
     *
     * @param waterMarks
     * @param dialogWidth
     * @param gapInLines
     *
     * @return Float value of dialog height to be painted
     */
    fun calculateDialogHeight(
        waterMarks: ArrayList<Data.WaterMark>,
        dialogWidth: Float,
        gapInLines: Float
    ): Float {
        var totalHeight = 0f
        waterMarks.forEach {
            if (it is Data.WaterMarkText) {
                totalHeight += (it.textSize * dialogWidth) + gapInLines
            }
        }
        return totalHeight
    }

    /**
     * calculate text lines Y-Axis point
     *
     * @param rootHeight Float value of the original bitmap
     * @param dialogHeight Float vale of the dialog's height
     * @param dialogWidth
     * @param gapInLines Float value of gaps between lines
     * @param waterMarks Arraylist of WaterMark
     *
     * @return ArrayList of Float
     */
    fun calculateLinesYAxisPoints(
        rootHeight: Float,
        dialogHeight: Float,
        dialogWidth: Float,
        gapInLines: Float,
        waterMarks: ArrayList<Data.WaterMark>
    ): ArrayList<Float> {
        val linesY = arrayListOf<Float>()
        waterMarks.forEach {
            if (it is Data.WaterMarkText) {
                val y = if (linesY.isEmpty()) {
                    (rootHeight + it.textSize + gapInLines) - dialogHeight
                } else {
                    linesY.last() + it.textSize + gapInLines
                }
                linesY.add(y)
            }
        }
        return linesY
    }

    /**
     * calculate text lines Y-Axis point
     *
     * @param rootHeight Float value of the original bitmap height
     * @param dialogHeight Float vale of the dialog's height
     * @param gapInLines Float value of gaps between lines
     * @param position
     * @param waterMarks Arraylist of WaterMark
     *
     * @return ArrayList of Float
     *
     */
    fun calculateLinesYAxisPoints(
        rootHeight: Float,
        dialogHeight: Float,
        gapInLines: Float,
        position: Data.WaterMarkPosition,
        waterMarks: ArrayList<Data.WaterMark>,
        overrideTextSize: Float = 0f,
    ): ArrayList<Float> {
        val linesY = arrayListOf<Float>()
        var _rootHeight = rootHeight - dialogHeight

        if (position == Data.WaterMarkPosition.TOP_LEFT || position == Data.WaterMarkPosition.TOP_RIGHT)
            _rootHeight = 0f

        waterMarks.forEach {

            if (it is Data.WaterMarkText) {

                /*val y = if (linesY.isEmpty()) {
                    _rootHeight + it.textSize + gapInLines
                } else {
                    linesY.last() + it.textSize + gapInLines
                }*/

                val y = if (linesY.isEmpty()) {
                    _rootHeight + overrideTextSize + overrideTextSize + gapInLines
                } else {
                    linesY.last() + overrideTextSize + gapInLines
                }

                Log.d("kaku", "calculateLinesYAxisPoints: Y point is $y")

                linesY.add(y)

            }
        }
        return linesY
    }

    /**
     * calculate text lines X-Axis point
     *
     * @param rootWidth Float value of the original bitmap width
     * @param dialogWidth Float vale of the dialog's width
     * @param textPadding Float value of text padding
     * @param position
     * @param waterMarks Arraylist of WaterMark
     *
     * @return ArrayList of Float
     *
     */
    fun calculateLinesXAxisPoints(
        rootWidth: Float,
        dialogWidth: Float,
        textPadding: Float,
        position: Data.WaterMarkPosition,
        waterMarks: ArrayList<Data.WaterMark>,
        hasLogo: Boolean = false,
        logoWidth: Float = 0f,
    ): ArrayList<Float> {
        val linesX = arrayListOf<Float>()
        var _rootWidth = 0f
        if (position == Data.WaterMarkPosition.TOP_RIGHT || position == Data.WaterMarkPosition.BOTTOM_RIGHT)
            _rootWidth = rootWidth - dialogWidth
        waterMarks.forEach {
            if (it is Data.WaterMarkText) {
                val x = _rootWidth + textPadding + logoWidth
                linesX.add(x)
            }
        }
        Log.d("mridx", "calculateLinesXAxisPoints: $_rootWidth")
        Log.d("mridx", "calculateLinesXAxisPoints: dialog w: $dialogWidth padding: $textPadding")
        return linesX
    }


}