package com.deathhorizon.comebacktolife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout

class PicprintActivity : AppCompatActivity() {
    lateinit var btn_come_back: TextView
    lateinit var view_pic_print: ImageView
    var imgarray = arrayOf(
        R.drawable.morning,
        R.drawable.afternoon,
        R.drawable.goodnight,
        R.drawable.oh
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picprint)
        btn_come_back = findViewById(R.id.btn_come_back)
        view_pic_print = findViewById(R.id.view_pic_print)
        btn_come_back.setOnClickListener{
            val intent = Intent()
            intent.setClass(baseContext, ChatroomActivity::class.java)
            startActivity(intent)
        }
        //view_pic_print.layoutParams= LinearLayout.LayoutParams(1000, 1000)
        //view_pic_print.x= 20F // setting margin from left
        //view_pic_print.y= 50F // setting margin from top
        // accessing our custom image which we added in drawable folder
        val imgResId = imgarray[2]
        view_pic_print.setImageResource(imgResId)
    }

}