package com.deathhorizon.comebacktolife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

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


        view_pic_print = findViewById(R.id.view_pic_print)
        btn_come_back = findViewById(R.id.btn_come_back)
        btn_come_back.setOnClickListener{
            val intent = Intent()
            intent.putExtra(TIMES, TIME_value)
            intent.setClass(baseContext, ChatroomActivity::class.java)
            startActivity(intent)
        }
        //view_pic_print.layoutParams= LinearLayout.LayoutParams(1000, 1000)
        //view_pic_print.x= 20F // setting margin from left
        //view_pic_print.y= 50F // setting margin from top
        // accessing our custom image which we added in drawable folder
        val numberval : Int = intent.getIntExtra(
            ChoicepicActivity.NUMBER,0 )
        TIME_value  = intent.getStringExtra(
            ChatroomActivity.TIMES)
        //depug------------------------
        /*Toast.makeText(
            baseContext,"onhere:\n"+
                    TIME_value,
            Toast.LENGTH_SHORT
        ).show()*/
        //depug------------------------
        val imgResId = imgarray[numberval]
        view_pic_print.setImageResource(imgResId)
    }

}