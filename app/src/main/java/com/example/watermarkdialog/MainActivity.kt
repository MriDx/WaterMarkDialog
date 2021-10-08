package com.example.watermarkdialog

import android.Manifest
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.mridx.watermarkdialog.BitmapUtils
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Type
import java.util.*

class MainActivity : AppCompatActivity() {


    var fileUri: Uri? = null

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (!it) return@registerForActivityResult
        processImage()
    }

    private fun processImage() {
        findViewById<ImageView>(R.id.imageView).setImageURI(fileUri)

        lifecycleScope.launch(Dispatchers.IO) {

            val bmp = Processor.process(
                view = findViewById(R.id.imageView),
                maxWidth = 1920.0f,
                maxHeight = 1920.0f,
                waterMarkData = Data.WaterMarkDataV2(
                    position = Data.WaterMarkPosition.BOTTOM_LEFT,
                    waterMarks = arrayListOf(
                        Data.WaterMarkText(
                            "Latitude : 26.000000", Color.BLACK
                        ),
                        Data.WaterMarkText(
                            "Longitude : 26.000000", Color.BLACK
                        ),
                        Data.WaterMarkText(
                            "Accuracy : 26 M", Color.BLACK
                        ),
                        Data.WaterMarkText(
                            text = "Uploaded By Mridul Baishya",
                            color = Color.RED,
                            textSize = 0.3f,
                            typeFace = Typeface.DEFAULT
                        ),
                        Data.WaterMarkText(
                            "Created By MriDx",
                            Color.YELLOW,
                            textSize = 0.5f,
                            typeFace = Typeface.create("OpenSans", Typeface.BOLD)
                        ),
                    )
                )
            )

            Utils.saveAsPNG(bmp, createFileSave())
            Utils.saveAsJPG(bmp, createFileSave())


        }

    }

    private fun createFileSave(): String {
        val f = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cap_${Date().time}.png")
        f.createNewFile()
        return f.path
    }

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPermission.launch(Manifest.permission.CAMERA)

        findViewById<Button>(R.id.btn).setOnClickListener {
            captureImage.launch(createFileUri())
        }


    }

    private fun createFileUri(): Uri {
        val f = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        f!!.mkdirs()
        val file = File(f.path, "t${Date().time}.jpg")
        fileUri = file.createFileUri()
        return fileUri!!
    }

    private fun File.createFileUri(): Uri {
        return FileProvider.getUriForFile(
            this@MainActivity,
            "${BuildConfig.APPLICATION_ID}.FileProvider",
            this
        )
    }


}