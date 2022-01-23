package com.example.calories


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.calories.DB.MyDbManager
import org.w3c.dom.Text
import java.io.*
import java.sql.Time
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool


class MainActivity : AppCompatActivity() {

    private lateinit var imageView : ImageView
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var resultLauncher : ActivityResultLauncher<Intent>
    private var imageUri : Uri? = null
    private var imageBitmap : Bitmap? = null
    private var locSound : Int? = null
    private var goodSound : Int? = null
    private var badSound : Int? = null
    private lateinit var soundPool : SoundPool

    private val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //при первом запуске будут спрашиваться разрешения если их не дали
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }

        // инициализация аудио
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
        locSound = soundPool.load(this,R.raw.accept,1)
        goodSound = soundPool.load(this,R.raw.good,1)
        badSound = soundPool.load(this,R.raw.warning,1)

        imageView = findViewById(R.id.photo)

        //переходим на активити с локацией
        buttonLocation.setOnClickListener {
            val intent = Intent(this, LoacationActivity::class.java)
            locSound?.let { soundPool.play(it,1f,1f,0,0,1f) }
            startActivity(intent)
        }

        //вызываем встроенную камеру только для фото
        buttonCamera.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                    //Toast.makeText(this, "Нет фото", Toast.LENGTH_SHORT).show()
            }
        }

        buttonStorage.setOnClickListener{
            pickImageGallery()
        }

        //если сфоткали то добавляем фотку на вью
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if(result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                imageUri = data?.data
                imageView.setImageURI(imageUri)
                imageBitmap = imageView.drawable.toBitmap()
            }
        }

        //переходим на активти с историей
        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        //добавляем в бд только если все поля заполнены
        buttonAdd.setOnClickListener{
            if(myImageViewText.text.toString() == "" && textName.text.toString() != "" && textCalories.text.toString() != "" && textConsist.text.toString() != "")
            {
                goodSound?.let { soundPool.play(it,1f,1f,0,0,1f) }
                myDbManager.openDb()
                imageBitmap
                myDbManager.insertToDb(textName.text.toString(), textCalories.text.toString(), textConsist.text.toString(), imageUri.toString())
            }
            else
            {
                badSound?.let { soundPool.play(it,1f,1f,0,0,1f) }
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_TAKE_PHOTO ->{
                if(resultCode == Activity.RESULT_OK && data !== null){
                    imageView.setImageBitmap(data.extras?.get("data") as Bitmap)
                    imageBitmap = data.extras?.get("data") as Bitmap
                    //imageBitmap = imageView.drawable.toBitmap()
                    myImageViewText.text = ""


                    //сделанное фото будем сохранять в памяти а не только в кэше приложения
                    val file = File(cacheDir, String.format(System.currentTimeMillis().toString(),"%.jpg")); // создать уникальное имя для файла основываясь на дате сохранения
                    var fOut = FileOutputStream(file);

                    imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
                    fOut.flush();
                    fOut.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме

                    imageUri = data?.data
                }
            }
//            else ->{
//                Toast.makeText(this, "Wrong request code", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        resultLauncher.launch(intent)
        myImageViewText.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }
}