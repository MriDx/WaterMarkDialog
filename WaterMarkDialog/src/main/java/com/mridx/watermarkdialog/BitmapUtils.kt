/*
 * Copyright (c) 2021.
 * @author MriDx
 */

package com.mridx.watermarkdialog

import android.graphics.*
import android.util.Log


object BitmapUtils {

    const val TAG = "kaku"

    fun addTags(
        bmp: Bitmap,
        waterMarkData: Data.WaterMarkDataV2
    ): Bitmap {

        val tmpBmp = bmp.copy(bmp.config, true)
        val bmpH = bmp.height
        val bmpW = bmp.width

        val dialogH: Float
        var dialogW = calculateDialogWidthFromBitmap(actualWidth = bmpW)

        val _textSizeTmp = dialogW * 0.1f
        var gapInLines = _textSizeTmp * 1.25f
        if (gapInLines > 30) gapInLines = 30f

        dialogH = calculateDialogHeight(
            waterMarks = waterMarkData.waterMarks,
            dialogWidth = dialogW,
            gapInLines = gapInLines
        ) + gapInLines


        val wmts = arrayListOf<Data.WaterMarkText>()
        waterMarkData.waterMarks.map {
            val wmt = it
            if (wmt is Data.WaterMarkText) {
                wmt.textSize = wmt.textSize * dialogW
                wmts.add(wmt)
            }
        }


        val linesY = Utils.calculateLinesYAxisPoints(
            rootHeight = bmpH.toFloat(),
            dialogHeight = dialogH,
            gapInLines = gapInLines,
            position = waterMarkData.position,
            waterMarks = waterMarkData.waterMarks
        )

        val textPadding = Paint().apply {
            textSize = _textSizeTmp
        }.measureText("  ")
        dialogW = calculateDialogWidth(waterMarks = waterMarkData.waterMarks, dialogWidth = dialogW)
        dialogW += (textPadding * 2f)

        val linesX = Utils.calculateLinesXAxisPoints(
            rootWidth = bmpW.toFloat(),
            dialogWidth = dialogW,
            textPadding = textPadding,
            position = waterMarkData.position,
            waterMarks = waterMarkData.waterMarks
        )

        val canvas = Canvas(tmpBmp)

        val dialogRect = drawDialog(
            canvas = canvas,
            position = waterMarkData.position,
            rootWidth = bmpW.toFloat(),
            rootHeight = bmpH.toFloat(),
            dialogWidth = dialogW,
            dialogHeight = dialogH
        )
        canvas.drawRect(dialogRect, Paint().apply {
            color = Color.WHITE
            alpha = 150
        })

        wmts.forEachIndexed { index, waterMarkText ->
            val paint = Paint().apply {
                textSize = waterMarkText.textSize
                color = waterMarkText.color
                typeface = waterMarkText.typeFace
            }
            Log.d("kaku", "addTags: ${waterMarkText.textSize}")
            canvas.drawText(waterMarkText.text, linesX[index], linesY[index], paint)
        }


        return tmpBmp


    }

    private fun drawDialog(
        canvas: Canvas,
        position: Data.WaterMarkPosition,
        rootWidth: Float,
        rootHeight: Float,
        dialogWidth: Float,
        dialogHeight: Float,
        hasLogo: Boolean = false,
    ): RectF {


        val left = calculateDialogLeft(
            position = position,
            rootWidth = rootWidth,
            dialogWidth = dialogWidth
        )
        val right = calculateDialogRight(
            position = position,
            rootWidth = rootWidth,
            dialogWidth = dialogWidth
        )
        val top = calculateDialogTop(
            position = position,
            rootHeight = rootHeight,
            dialogHeight = dialogHeight,
            hasLogo = hasLogo,
        )
        val bottom = calculateDialogBottom(
            position = position,
            rootHeight = rootHeight,
            dialogHeight = dialogHeight
        )
        val dialogPaint = Paint().apply {
            color = Color.WHITE
            alpha = 150
        }
        return RectF(left, top, right, bottom)
        /*return canvas.drawRect(
            left,
            top,
            right,
            bottom,
            dialogPaint
        )*/
    }


    @Deprecated("")
    private fun calculateDialogHeight(
        waterMarks: ArrayList<Data.WaterMark>,
        dialogWidth: Float,
        gapInLines: Float,
        overrideTextSize: Float = 0f,
    ): Float {
        var totalHeight = gapInLines + overrideTextSize + overrideTextSize
        waterMarks.forEach {
            if (it is Data.WaterMarkText) {
                //totalHeight += (it.textSize * dialogWidth) + gapInLines
                totalHeight += overrideTextSize + gapInLines
            }
        }
        return totalHeight
    }


    @Deprecated("")
    private fun calculateDialogWidthFromBitmap(actualWidth: Int): Float {
        return when {
            /*actualWidth > 4000 -> {
                actualWidth * 0.3f
            }
            actualWidth > 3800 -> {
                actualWidth * 0.45f
            }
            actualWidth > 3600 -> {
                actualWidth * 0.3f
            }
            actualWidth > 3400 -> {
                actualWidth * 0.25f
            }
            actualWidth > 3000 -> {
                actualWidth * 0.2f
            }
            actualWidth > 2600 -> {
                actualWidth * 05f
            }*/
            actualWidth > 2200 -> {
                actualWidth * 0.1f
            }

            actualWidth > 1920 -> {
                actualWidth * 0.15f
            }

            actualWidth > 1600 -> {
                actualWidth * 0.2f
            }

            actualWidth > 1366 -> {
                actualWidth * 0.25f
            }

            actualWidth > 1080 -> {
                actualWidth * 0.3f
            }

            actualWidth > 720 -> {
                actualWidth * 0.35f
            }

            else -> {
                actualWidth * 0.4f
            }
        }
    }

    @Deprecated("")
    private fun calculateDialogWidth(
        waterMarks: ArrayList<Data.WaterMark>,
        dialogWidth: Float
    ): Float {
        var dw = 0f
        waterMarks.forEach {
            val wmt = it
            if (wmt is Data.WaterMarkText) {
                val measured =
                    measureText(text = wmt.text, txtSize = wmt.textSize, typeFace = wmt.typeFace)
                if (measured > dw) dw = measured
            }
        }
        return dw
    }

    @Deprecated("")
    private fun measureText(text: String, txtSize: Float, typeFace: Typeface): Float {
        return Paint().apply {
            textSize = txtSize
            typeface = typeFace
        }.measureText(text)
    }

    fun addTags(
        bmp: Bitmap,
        waterMarkData: Data.WaterMarkData,
        textSizeRatio: Float = 0.1f,
        typeFace: Typeface,
    ): Bitmap? {

        val tmpBmp = bmp.copy(bmp.config, true)
        val bmpH = bmp.height
        val bmpW = bmp.width

        val dialogH: Float
        var dialogW = calculateDialogWidthFromBitmap(actualWidth = bmpW)

        val _textSize = dialogW * textSizeRatio

        val lines = tagLines(waterMarks = waterMarkData.waterMarks)
        val gap = _textSize * 1.25f
        //dialogH = (_textSize * (lines.size + 2))
        dialogH = (gap * (lines.size + 1))

        val rootCanvas = Canvas(tmpBmp)

        val dialogPaint = Paint().apply {
            color = Color.WHITE
            alpha = 150
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = _textSize
            typeface = typeFace
        }

        val linesY = calculateLinesYAxisPoints(
            rootHeight = bmpH,
            textSize = _textSize,
            dialogHeight = dialogH,
            linesSize = lines.size,
            position = waterMarkData.position
        )

        dialogW = calculateDialogWidth(textLines = lines, textPaint = textPaint)

        val textPadding = textPaint.measureText("  ")
        dialogW += (textPadding * 2f) //


        val dialogLeft = calculateDialogLeft(
            position = waterMarkData.position,
            rootWidth = bmpW.toFloat(),
            dialogWidth = dialogW
        )
        val dialogTop = calculateDialogTop(
            position = waterMarkData.position,
            rootHeight = bmpH.toFloat(),
            dialogHeight = dialogH,
            hasLogo = false,
        )
        val dialogRight = calculateDialogRight(
            position = waterMarkData.position,
            rootWidth = bmpW.toFloat(),
            dialogWidth = dialogW
        )
        val dialogBottom = calculateDialogBottom(
            position = waterMarkData.position,
            rootHeight = bmpH.toFloat(),
            dialogHeight = dialogH
        )
        rootCanvas.drawRect(
            dialogLeft,
            dialogTop,
            dialogRight,
            dialogBottom,
            dialogPaint
        )
        /*rootCanvas.drawRect(
            dialogLeft, //left
            (bmpH - dialogH), //top
            dialogW, //right
            bmpH.toFloat(), //bottom
            dialogPaint
        )*/

        lines.forEachIndexed { index, s ->
            rootCanvas.drawText(s, textPadding, linesY[index], textPaint)
        }

        return tmpBmp

    }

    private fun calculateDialogBottom(
        position: Data.WaterMarkPosition,
        rootHeight: Float,
        dialogHeight: Float
    ): Float {
        return when (position) {
            Data.WaterMarkPosition.TOP_LEFT,
            Data.WaterMarkPosition.TOP_RIGHT -> {
                dialogHeight
            }

            Data.WaterMarkPosition.BOTTOM_LEFT,
            Data.WaterMarkPosition.BOTTOM_RIGHT -> {
                rootHeight
            }
        }
    }

    private fun calculateDialogRight(
        position: Data.WaterMarkPosition,
        rootWidth: Float,
        dialogWidth: Float
    ): Float {
        return when (position) {
            Data.WaterMarkPosition.TOP_LEFT,
            Data.WaterMarkPosition.BOTTOM_LEFT -> {
                dialogWidth
            }

            Data.WaterMarkPosition.TOP_RIGHT,
            Data.WaterMarkPosition.BOTTOM_RIGHT -> {
                rootWidth
            }
        }
    }

    private fun calculateDialogTop(
        position: Data.WaterMarkPosition,
        rootHeight: Float,
        dialogHeight: Float,
        hasLogo: Boolean = false,
    ): Float {
        return when (position) {
            Data.WaterMarkPosition.TOP_LEFT,
            Data.WaterMarkPosition.TOP_RIGHT -> {
                0f
            }

            Data.WaterMarkPosition.BOTTOM_LEFT,
            Data.WaterMarkPosition.BOTTOM_RIGHT -> {
                rootHeight - dialogHeight
            }
        }
    }

    private fun calculateDialogLeft(
        position: Data.WaterMarkPosition,
        rootWidth: Float,
        dialogWidth: Float
    ): Float {
        return when (position) {
            Data.WaterMarkPosition.TOP_LEFT,
            Data.WaterMarkPosition.BOTTOM_LEFT -> {
                0f
            }

            Data.WaterMarkPosition.TOP_RIGHT,
            Data.WaterMarkPosition.BOTTOM_RIGHT -> {
                rootWidth - dialogWidth
            }
        }
    }

    private fun calculateDialogWidth(
        textLines: java.util.ArrayList<String>,
        textPaint: Paint
    ): Float {
        var w = 0f
        textLines.forEach {
            val m = textPaint.measureText(it)
            if (m > w)
                w = m
        }
        return w
    }

    private fun calculateLinesYAxisPoints(
        rootHeight: Int,
        textSize: Float,
        dialogHeight: Float,
        linesSize: Int,
        position: Data.WaterMarkPosition
    ): ArrayList<Float> {
        val linesY = arrayListOf<Float>()

        for (i in 0 until linesSize) {
            val y = if (i == 0) {
                ((rootHeight + textSize * 1.5f) - dialogHeight)
            } else {
                linesY.last() + (textSize * 1.25f)
            }
            linesY.add(y)
        }
        return linesY
    }

    @Deprecated("")
    private fun calculateLinesYAxisPoints(
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


    private fun tagLines(waterMarks: Map<String, String>): ArrayList<String> {
        val lines = arrayListOf<String>()
        waterMarks.entries.forEach {
            lines.add("${it.key}: ${it.value}")
        }
        return lines
    }


    fun addTagsV2(bitmap: Bitmap, waterMarkData: Data.WaterMarkDataV2): Bitmap {

        val tmpBmp = bitmap.copy(bitmap.config, true)
        val bmpH = bitmap.height
        val bmpW = bitmap.width

        Log.d(TAG, "addTagsV2: bitmap size is H $bmpH W $bmpW")

        val dialogH: Float
        var dialogW = calculateDialogWidthFromBitmap(actualWidth = bmpW)

        dialogW = /*(bmpW * 0.65f)*/ bmpW.toFloat()

        Log.d(TAG, "addTagsV2: 65 % of total width is $dialogW")

//        val _textSizeTmp = dialogW * 0.1f
//        var gapInLines = _textSizeTmp * 1.25f
//        if (gapInLines > 30) gapInLines = 30f

        val _textSizeTmp = dialogW * 0.03f
        var gapInLines = _textSizeTmp * 0.025f
        if (gapInLines > 20) gapInLines = 20f

        Log.d(TAG, "addTagsV2: text size is ${_textSizeTmp}")
        Log.d(TAG, "addTagsV2: gap between is ${gapInLines}")
        Log.d(TAG, "addTagsV2: gap between is ${gapInLines}")

        dialogH = calculateDialogHeight(
            waterMarks = waterMarkData.waterMarks,
            dialogWidth = dialogW,
            gapInLines = gapInLines,
            overrideTextSize = _textSizeTmp
        ) + gapInLines

        Log.d(TAG, "addTagsV2: dialog height is ${dialogH}")


        val wmts = arrayListOf<Data.WaterMarkText>()
        waterMarkData.waterMarks.map {
            val wmt = it
            if (wmt is Data.WaterMarkText) {
                wmt.textSize = wmt.textSize * dialogW
                wmts.add(wmt)
            }
        }

        val linesY = Utils.calculateLinesYAxisPoints(
            rootHeight = bmpH.toFloat(),
            dialogHeight = dialogH,
            gapInLines = gapInLines,
            position = waterMarkData.position,
            waterMarks = waterMarkData.waterMarks,
            overrideTextSize = _textSizeTmp,
        )

        val textPadding = Paint().apply {
            textSize = _textSizeTmp
        }.measureText("  ")

        //dialogW = calculateDialogWidth(waterMarks = waterMarkData.waterMarks, dialogWidth = dialogW)
        //dialogW += (textPadding * 2f)

        val linesX = Utils.calculateLinesXAxisPoints(
            rootWidth = bmpW.toFloat(),
            dialogWidth = dialogW,
            textPadding = textPadding,
            position = waterMarkData.position,
            waterMarks = waterMarkData.waterMarks,
            logoWidth = if (waterMarkData.logo != null) dialogH / 2 else 0f,
        )


        val canvas = Canvas(tmpBmp)

        val dialogRect = drawDialog(
            canvas = canvas,
            position = waterMarkData.position,
            rootWidth = bmpW.toFloat(),
            rootHeight = bmpH.toFloat(),
            dialogWidth = dialogW,
            dialogHeight = dialogH,
            hasLogo = false,
            //hasLogo = waterMarkData.logo != null,
        )


        canvas.drawRect(dialogRect, Paint().apply {
            color = Color.WHITE
            alpha = 150
        })

        wmts.forEachIndexed { index, waterMarkText ->
            val paint = Paint().apply {
                // textSize = waterMarkText.textSize
                textSize = _textSizeTmp
                color = waterMarkText.color
                //typeface = waterMarkText.typeFace
                typeface = Typeface.SERIF
            }
            canvas.drawText(waterMarkText.text, linesX[index], linesY[index], paint)
        }

        if (waterMarkData.logo != null) {

            val logoBitmap = getResizedBitmap(
                bm = waterMarkData.logo!!.imageBitmap,
                newHeight = (dialogH / 2).toInt(),
                newWidth = (dialogH / 2).toInt(),
            )

            /*val position = when (waterMarkData.logo!!.position) {
                Data.WaterMarkPosition.BOTTOM_LEFT -> {

                }
                Data.WaterMarkPosition.BOTTOM_RIGHT -> {

                }
                Data.WaterMarkPosition.TOP_LEFT -> {
                    arrayOf(0f, 0f)
                }
                Data.WaterMarkPosition.TOP_RIGHT -> {

                }
            }*/


            canvas.drawBitmap(
                logoBitmap,
                dialogRect.left,
                (dialogRect.top + (dialogH / 4)).toFloat(),
                Paint()
            )

        }



        return tmpBmp


    }


    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
    }


}