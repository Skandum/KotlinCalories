package com.example.calories

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import com.example.calories.DB.MyDbManager
import kotlinx.android.synthetic.main.activity_history.*
import androidx.constraintlayout.widget.ConstraintLayout

import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class HistoryActivity : AppCompatActivity() {

    private var mLayout : LinearLayout? = null
    private val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        mLayout = findViewById(R.id.myLayout)


//        for(i in 1..50){
//            val text1 = TextView(this)
//            text1.layoutParams = mLayout?.layoutParams
//            text1.text = "New TextView $i"
//            myLayout.addView(text1)
//        }

        myDbManager.openDb()

        buttonClear.setOnClickListener {
            myDbManager.clearDb()
            myLayout.removeAllViewsInLayout()
        }


        try {
            val listName = myDbManager.getDbItems(1)
            val listCal = myDbManager.getDbItems(2)
            val listDesc = myDbManager.getDbItems(3)
            val listImage = myDbManager.getDbItems(4)


            //берем из базы по очереди каждую запись и через текствью выводим
            //!!!не менять матчпаренты чтобы не съехало
            for(i in 0..listName.size-1)
            {
                val line = LinearLayout(this)
                line.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                line.orientation = LinearLayout.VERTICAL
                line.gravity = Gravity.CENTER
                val text = TextView(this)
                val img = ImageView(this)
                text.layoutParams = line.layoutParams
                text.textSize = 24F
                text.setPadding(10,10,10,10)
                //text.gravity = Gravity.CENTER
                text.maxLines = 12
                val margin50inDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
                ).toInt()
                val margin60inDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 160f, resources.displayMetrics
                ).toInt()

                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(margin60inDp, margin50inDp, margin60inDp, margin50inDp)
                layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                //text.setLayoutParams(layoutParams)
                text.text = "Имя: ${listName[i]}\nКалории: ${listCal[i]} ккал\nСостав:\n${listDesc[i]}"
                myLayout.addView(text)


                //почему то блин крашится когда > 1 юри в базе
                text.setOnClickListener{
                    val uri = Uri.parse(listImage[0])
                    if(uri !== null)
                    {
                        img.setImageURI(uri)
                        myLayout.addView(img)
                    }
                }
            }
        }
        catch (e: Exception){
            Toast.makeText(this, "Пусто)))", Toast.LENGTH_SHORT).show()
        }
    }
}