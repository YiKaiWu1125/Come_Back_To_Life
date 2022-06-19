package com.deathhorizon.comebacktolife

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import java.util.*
import kotlin.collections.ArrayList


class ChatroomActivity : AppCompatActivity(), LocationListener {
    private lateinit var messageList: ArrayList<Message>
    private lateinit var tv_user_id: TextView
    private lateinit var mDbRef: DatabaseReference
    private lateinit var btn_confirm: Button
    private lateinit var et_msg: EditText
    private lateinit var tv_bf_msg: TextView
    private lateinit var my_uid: String
    private lateinit var isinit: String
    private lateinit var btn_sos: Button
    private lateinit var btn_pic: Button
    lateinit var locmgr: LocationManager
    var nowloc_latitude : String? = null
    var nowloc_longitude : String? = null
    var go_latitude : String? = null
    var go_longitude : String? = null
    var go_who : String? = null
    var bo : Int? = 0
    private var limit = 30.0
    var myname : String = "我"
    companion object {
        val NUMBER: String = "NUMBER"
        val TIMES: String = "TIMES"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isinit = "1"
        setContentView(R.layout.activity_chatroom)
        tv_user_id = findViewById(R.id.user_id)

        tv_bf_msg = findViewById(R.id.bf_message)
        val k :String? = intent.getStringExtra(
            MainActivity.USERNAME)
        myname = if(k!=null&&k!=""){
            k
        }else{
            "我"
        }
        if (ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        } else {
            initLoc()
        }


        // val userName = intent.getStringExtra("userName")
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
        my_uid = senderUid
        tv_user_id.setText(senderUid.substring(0,3))

        messageList = ArrayList()
        mDbRef = FirebaseDatabase.getInstance("https://come-back-to-life-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
//        Toast.makeText(this, mDbRef.key, Toast.LENGTH_SHORT).show()



        val numberval :Int  = intent.getIntExtra(
            ChoicepicActivity.NUMBER ,-1)
        if(numberval != -1){
            val messageObj = Message(numberval.toString(), senderUid,gettime()+"_電源:"+getBatteryLevel().toString()+"%","","pic")
            mDbRef.child("chats").child("messages").push()
                .setValue(messageObj)
        }

        mDbRef.child("chats").child("messages").orderByKey().limitToLast(30).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //Battery
                if (getBatteryLevel() > limit ){
                    val intent = Intent()
                    intent.setClass(baseContext, MainActivity::class.java)
                    startActivity(intent)
                }
                //-------------------------------
                messageList.clear()
                for(postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }

                //toast message
                updateMsgUI()
                val rtime :String?  = intent.getStringExtra(
                    PicprintActivity.TIMES)
                if(messageList[messageList.size - 1].longitude=="pic" && rtime != messageList[messageList.size - 1].time){
                    if(messageList[messageList.size-1].senderId != my_uid){
                        //******debug------------------------
                        /*Toast.makeText(
                            baseContext,"rtime:\n"+
                            if(rtime==null){
                                           "null"
                                           }else{
                                rtime
                                                },
                            Toast.LENGTH_SHORT
                        ).show()
                        Toast.makeText(
                            baseContext,"messageList:\n"+
                            if(messageList[messageList.size - 1].time==null){
                                                                            "null"
                                                                            }else{
                                messageList[messageList.size - 1].time
                                                                                 },
                            Toast.LENGTH_SHORT
                        ).show()*/
                        //******debug------------------------
                        val intent = Intent()
                        intent.setClass(baseContext, PicprintActivity::class.java)
                        var str : String= if(messageList[messageList.size - 1].message==null){
                            "0"
                        } else{
                            messageList[messageList.size - 1].message
                        }
                        intent.putExtra(NUMBER, str.toInt())
                        intent.putExtra(TIMES, messageList[messageList.size - 1].time)
                        startActivity(intent)
                    }
                }
                if (messageList[messageList.size-1].senderId != my_uid && messageList[messageList.size - 1].message!="" && isinit != "1"&& getBatteryLevel() < limit &&messageList[messageList.size - 1].longitude!="pic") {
                    if (messageList[messageList.size - 1].latitude == null) {
                        Toast.makeText(
                            baseContext,
                            messageList[messageList.size - 1].senderId.toString()
                                .substring(0, 3) + ":\n" + messageList[messageList.size - 1].message,
                            Toast.LENGTH_SHORT
                        ).show()
                        sendNotification(
                            baseContext,
                            messageList[messageList.size - 1].message.toString()
                        )
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        et_msg = findViewById(R.id.message)
        btn_confirm = findViewById(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            val message = et_msg.text.toString()
            if(message!="") {
                val messageObj = Message(message, senderUid,gettime()+"_電源:"+getBatteryLevel().toString()+"%","","")
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
                et_msg.setText("")
            }
        }

        btn_sos = findViewById(R.id.btn_SOS)
        btn_sos.setOnClickListener {
            if(bo==1&&go_who!=my_uid){
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://maps.google.com/maps?" +
                        "saddr=" + nowloc_latitude + "," + nowloc_longitude+"&"+
                        "daddr="+go_latitude+","+go_longitude)
                startActivity(intent)
            }
            else if(bo==1&&go_who==my_uid){
                val messageObj = Message("NO", senderUid,gettime(),nowloc_latitude,nowloc_longitude)
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
            }
            else{
                val messageObj = Message("YES", senderUid,gettime(),nowloc_latitude,nowloc_longitude)
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
            }
        }
        btn_pic = findViewById(R.id.btn_pic)
        btn_pic.setOnClickListener {
            val intent = Intent()
            //val str_u: String = "hi"
            //intent.putExtra(NUMBER,  str_u)
            intent.setClass(this, ChoicepicActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode,
            permissions, grantResults)

        if ((grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
            initLoc()
        }
    }

    private fun initLoc() {
        locmgr = getSystemService(LOCATION_SERVICE) as
                LocationManager

        var loc: Location? = null
        try {
            loc = locmgr.getLastKnownLocation(
                LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
        }

        if (loc != null) {
            showLocation(loc)
        } else {
            //tv_loc.text = "Cannot get location!"
        }

        try {
            locmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000, 1f, this)
        } catch (e: SecurityException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locmgr.removeUpdates(this)
    }

    override fun onLocationChanged(loc: Location) {
        showLocation(loc)
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    private fun showLocation(loc: Location){
        nowloc_latitude = loc.latitude.toString()
        nowloc_longitude =loc.longitude.toString()
    }


    private fun gettime():String{
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        val mon: String = getzero(c.get(Calendar.MONTH))
        val day: String = getzero(c.get(Calendar.DATE))
        val hour: String = getzero(c.get(Calendar.HOUR_OF_DAY))
        val min: String = getzero(c.get(Calendar.MINUTE))
        val sec: String =getzero(c.get(Calendar.SECOND))
        return mon+"月"+day+"日_"+hour+":"+min+":"+sec
    }
    private fun getzero(valval: Int):String{
        return if(valval<10){
            "0"+valval.toString()
        }else {
            return valval.toString()
        }
    }
    private fun updateMsgUI(){
        tv_bf_msg.setText("")
        go_latitude = null
        go_longitude = null
        go_who = null
        bo = 0
        for(msg in messageList){
            if (msg.latitude != null ) {
                if(msg.message=="YES"){
                    if(bo==0){
                        go_latitude = msg.latitude
                        go_longitude = msg.longitude
                        go_who = msg.senderId
                        bo = 1
                    }
                }
                else{
                    if(go_who==msg.senderId){
                        go_latitude = null
                        go_longitude = null
                        go_who = null
                        bo = 0
                    }
                }
            }
        }
        var k = 1
        for(msg in messageList.reversed()) {
            if(msg.longitude=="pic") {
                var senderIdSlice: String
                senderIdSlice = if (msg.senderId == my_uid) {
                    myname
                } else {
                    //msg.senderId.toString().substring(0,3)
                    "匿名"
                }
                tv_bf_msg.append(senderIdSlice + ": " + "傳送了一張貼圖" + "\n" + msg.time + "\n\n")
                continue
            }
            if (msg.latitude == null )  {
                var senderIdSlice: String
                senderIdSlice = if (msg.senderId == my_uid) {
                    myname
                } else {
                    //msg.senderId.toString().substring(0,3)
                    "匿名"
                }
                val time = if (msg.time == null) {
                    "XX月XX日_XX:XX:XX\n<舊版不支援日期功能>"
                } else {
                    msg.time
                }
                tv_bf_msg.append(senderIdSlice + ": " + msg.message + "\n" + time + "\n\n")
            }
            else {
                k=0
                var senderIdSlice: String
                senderIdSlice = if (msg.senderId == my_uid) {
                    myname
                } else {
                    msg.senderId.toString().substring(0,3)+" "
                }
                if(msg.message=="NO"){
                    senderIdSlice = senderIdSlice +"已獲得行動電源."
                }
                else{
                    senderIdSlice=senderIdSlice+"需要行動電源."
                    if(msg.senderId!=my_uid){
                        senderIdSlice=senderIdSlice+"\n<點擊 前往救援 即可前往救援.>"
                    }
                    else{
                        senderIdSlice=senderIdSlice+"\n<再次點擊 即取消往救援.>"
                    }
                }
                val time = if (msg.time == null) {
                    "XX月XX日_XX:XX:XX"
                } else {
                    msg.time
                }
                tv_bf_msg.append(senderIdSlice + "\n" + time + "\n\n")
            }

        }
        isinit = "0"
        if(bo==1){
            if(go_who==my_uid){
                btn_sos.setBackgroundColor(Color.parseColor("#FF737210"))
                btn_sos.setText("等待救援")

            }
            else{
                btn_sos.setBackgroundColor(Color.parseColor("#FF737572"))
                btn_sos.setText("前往救援")

            }
        }
        else{
            btn_sos.setBackgroundColor(Color.parseColor("#FFA8A5A5"))
            btn_sos.setText("徵求行動電源SOS")
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
    private fun sendNotification(context: Context, msg: String) {
        val intent = Intent()
        intent.setClass(context, MainActivity::class.java)
        //intent.putExtra(MainActivity.EXTRA_MSG, msg)
        val pi = PendingIntent.getActivity(context,
            0, intent, PendingIntent.FLAG_IMMUTABLE)
        var notification: Notification? = null
        try {
            notification = getNotification(context, pi,
                context.getString(R.string.app_name), msg)
        } catch (e: Exception) {
        }
        if (notification != null) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
            notificationManager.notify(1, notification)
        }
    }

    private fun getNotification(context: Context, pi: PendingIntent,
                                title: String, msg: String): Notification? {

        var notification: Notification? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val channel = NotificationChannel("lincyu.alarmclock",
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW)
                channel.setShowBadge(false)
                val notificationManager: NotificationManager =
                    context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
                notification = Notification.Builder(context, "lincyu.alarmclock")
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(msg)
                    .setWhen(System.currentTimeMillis())
                    .build()
            } else if (Build.VERSION.SDK_INT >= 16){
                notification = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(msg)
                    .setWhen(System.currentTimeMillis())
                    .build()
            }
        } catch (throwable: Throwable) {
            return null
        }
        return notification
    }
}

private fun LocationManager.requestLocationUpdates(gpsProvider: String, i: Int, fl: Float, chatroomActivity: ChatroomActivity) {

}
