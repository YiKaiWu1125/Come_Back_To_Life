package com.deathhorizon.comebacktolife

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlin.math.sign

class MainActivity : AppCompatActivity() {
    private lateinit var btn_enter_chatroom : Button
    private lateinit var auth : FirebaseAuth
    private var limit = 30.0 //如需demo請自行修改進入的電量條件值
    companion object {
        val USERNAME: String = "USERNAME"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth


        btn_enter_chatroom = findViewById(R.id.enter_chatroom)
        btn_enter_chatroom.setOnClickListener({enterChatroom()})

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            signInAnonymously()
        }
    }
    private fun signInAnonymously() {
        // [START signin_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("CBTL", "signInAnonymously:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END signin_anonymously]
    }


    fun enterChatroom(){
        val batteryPct : Float = getBatteryLevel()
        Log.d("BATTERYLOG", batteryPct.toString())
        if (batteryPct > limit ){
            toast( this.getString(R.string.battery_too_high) )
        }
        else{

            startChatRoom()
        }
    }
    private fun getBatteryLevel(): Float {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        var batteryLevel = -1
        var batteryScale = 1
        if (batteryStatus != null) {
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel)
            batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale)
        }
        return batteryLevel / batteryScale.toFloat() * 100
    }
    private fun startChatRoom() {
        signInAnonymously()
        if(auth.currentUser == null){
            Toast.makeText( this, "not loggin", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent()


        intent.setClass(this, ChatroomActivity::class.java)
        startActivity(intent)
    }
    fun toast(msg: String) {
        val context: Context = applicationContext
        val text: CharSequence = msg
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }

}