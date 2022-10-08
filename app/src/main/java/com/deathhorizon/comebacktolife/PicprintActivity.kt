package com.deathhorizon.comebacktolife

import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PicprintActivity : AppCompatActivity() {
    lateinit var btn_come_back: Button
    lateinit var view_pic_print: ImageView

    companion object {
        val TIMES: String = "TIME"
    }
    var imgarray = arrayOf(
        R.drawable.morning,
        R.drawable.afternoon,
        R.drawable.goodnight,
        R.drawable.oh
    )

    var TIME_value : String? =" "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picprint)
        val go_back_run: Runnable = object : Runnable {
            override fun run() {
                finish()
            }
        }
        val handler = Handler()
        handler.postDelayed(go_back_run, 3000)
        view_pic_print = findViewById(R.id.view_pic_print)
        btn_come_back = findViewById(R.id.btn_come_back)
        btn_come_back.setOnClickListener{
            handler.removeCallbacks(go_back_run)
            finish()
        }
        val numberval : Int = intent.getIntExtra(
            ChoicepicActivity.NUMBER,0 )

        TIME_value  = intent.getStringExtra(
            ChatroomActivity.TIMES)
        val imgResId = imgarray[numberval]
        view_pic_print.setImageResource(imgResId)
    }

}