package com.deathhorizon.comebacktolife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class ChoicepicActivity : AppCompatActivity() {
    lateinit var btn_come_back: Button
    lateinit var view_pic_print: ImageView
    var imgarray = arrayOf(
        R.drawable.morning,
        R.drawable.afternoon,
        R.drawable.goodnight,
        R.drawable.oh
    )

    companion object {
        val NUMBER: String = "NUMBER"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choicepic)

        btn_come_back = findViewById(R.id.btn_come_back)
        btn_come_back.setOnClickListener{
            val intent = Intent()
            intent.putExtra(NUMBER, -1)
            setResult(RESULT_OK, intent)
            finish()
        }
        view_pic_print = findViewById(R.id.img_morning)
        view_pic_print.setOnClickListener{
            val intent = Intent()
            intent.putExtra(NUMBER, 0)
            setResult(RESULT_OK, intent)
            finish()
        }
        view_pic_print = findViewById(R.id.img_afternoon)
        view_pic_print.setOnClickListener{
            val intent = Intent()
            intent.putExtra(NUMBER, 1)
            setResult(RESULT_OK, intent)
            finish()
        }
        view_pic_print = findViewById(R.id.img_night)
        view_pic_print.setOnClickListener{
            val intent = Intent()
            intent.putExtra(NUMBER, 2)
            setResult(RESULT_OK, intent)
            finish()
        }
        view_pic_print = findViewById(R.id.img_oh)
        view_pic_print.setOnClickListener{
            val intent = Intent()
            intent.putExtra(NUMBER, 3)
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}