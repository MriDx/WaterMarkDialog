package com.example.watermarkdialog

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class MainActivity : AppCompatActivity() {


    var fileUri: Uri? = null

    var position = Data.WaterMarkPosition.BOTTOM_LEFT
    var HQ = false

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (!it) return@registerForActivityResult
        processImage()
    }

    private fun processImage() {
        findViewById<ImageView>(R.id.imageView).setImageURI(fileUri)

        lifecycleScope.launch(Dispatchers.IO) {

            //val file = File(fileUri!!.path)


            /*val bmp = Processor.process(
                file = file, maxHeight = 720f, maxWidth = 720f, waterMarkData = Data.WaterMarkData(
                    waterMarks = mapOf(
                        "Hello" to "World",
                        "Game" to "Changer",
                        "Processed via" to "WaterMarkDialog-MriDx"
                    ), position = Data.WaterMarkPosition.BOTTOM_LEFT
                ), typeface = ResourcesCompat.getFont(this@MainActivity, R.font.aclonica)!!
            ) ?: throw Exception("Image processing error !")*/

            val bmp = Processor.processV2(
                view = findViewById<ImageView>(R.id.imageView),
                maxWidth = 720.0f,
                maxHeight = 720.0f,
                waterMarkData = Data.WaterMarkDataV2(
                    position = position, waterMarks = arrayListOf(
                        Data.WaterMarkText(
                            "Latitude : 26.000000", Color.BLACK
                        ),
                        Data.WaterMarkText(
                            "Longitude : 26.000000",
                            Color.BLACK,
                            typeFace = ResourcesCompat.getFont(
                                this@MainActivity, R.font.roboto_regular
                            ) ?: Typeface.SERIF
                        ),
                        Data.WaterMarkText(
                            "Accuracy : 26 M", Color.BLACK, typeFace = ResourcesCompat.getFont(
                                this@MainActivity, R.font.aclonica
                            )!!
                        ),
                        Data.WaterMarkText(
                            text = "Uploaded By Mridul Baishya",
                            color = Color.RED,
                            textSize = 0.15f,
                            typeFace = Typeface.DEFAULT
                        ),
                        /*Data.WaterMarkText(
                            "Created By MriDx",
                            Color.YELLOW,
                            textSize = 0.12f,
                        ),*/
                    ),
                    logo = Data.WaterMarkImage(
                        imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.jjm_logo),
                    )
                ),
            ) ?: throw Exception("Image processing error !")

            val img = createFileSave()

            if (HQ) Utils.saveAsPNG(bmp, img)
            else Utils.saveAsJPG(bmp, img)

            val imgFile = File(img)

            withContext(Dispatchers.Main) {
                findViewById<ImageView>(R.id.imageView).setImageURI(imgFile.toUri())
            }

        }

    }

    /*private fun createFileSave(): String {
        val f = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cap_${Date().time}.png")
        f.createNewFile()
        return f.path
    }*/

    private fun createFileSave(): String {
        val fp = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "processed")
        fp.mkdirs()
        val f = File(fp.path, "${position}_${Date().time}.png")
        f.createNewFile()
        return f.path
    }

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPermission.launch(Manifest.permission.CAMERA)

        findViewById<Button>(R.id.btnTopLeft).setOnClickListener {
            position = Data.WaterMarkPosition.TOP_LEFT
            captureImage.launch(createFileUri())
        }

        findViewById<Button>(R.id.btnTopRight).setOnClickListener {
            position = Data.WaterMarkPosition.TOP_RIGHT
            captureImage.launch(createFileUri())
        }

        findViewById<Button>(R.id.btnBottomLeft).setOnClickListener {
            position = Data.WaterMarkPosition.BOTTOM_LEFT
            captureImage.launch(createFileUri())
        }

        findViewById<Button>(R.id.btnBottomRight).setOnClickListener {
            position = Data.WaterMarkPosition.BOTTOM_RIGHT
            captureImage.launch(createFileUri())
        }

        findViewById<SwitchMaterial>(R.id.switchMaterial).setOnCheckedChangeListener { compoundButton, b ->
            HQ = b
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
            this@MainActivity, "${BuildConfig.APPLICATION_ID}.FileProvider", this
        )
    }


}