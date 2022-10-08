package com.deathhorizon.comebacktolife

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ChatroomActivity : AppCompatActivity(), LocationListener {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter : MessageAdapter

    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var my_uid: String
    private lateinit var btn_sos: Button
    private lateinit var btn_pic: ImageView
    lateinit var locmgr: LocationManager
    var nowloc_latitude : String? = null
    var nowloc_longitude : String? = null
    var go_latitude : String? = null
    var go_longitude : String? = null
    var go_who : String? = null
    var bo : Int? = 0
    private var limit = 30.0 //如需demo請自行修改進入的電量條件值
    var myname : String = "我"
    companion object {
        val NUMBER: String = "NUMBER"
        val TIMES: String = "TIMES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)

        messageList = ArrayList()

        messageAdapter = MessageAdapter(this, messageList)

        //請求位置權限
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

        //隨時檢查電量然後把人踢掉
        thread {
            run(){
                try {
                    while (!this.isFinishing) {
                        if (getBatteryLevel() > limit ){
                            finish()
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }


        //取得FirebaseData
        mDbRef = FirebaseDatabase.getInstance("https://come-back-to-life-default-rtdb.asia-southeast1.firebasedatabase.app").reference


        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            if(message!="") {
//                val messageObj = Message(message, my_uid,gettime()+"_電源:"+getBatteryLevel().toString()+"%","","")
                val messageObj = Message(message, FirebaseAuth.getInstance().currentUser!!.uid, gettime() , getBatteryLevel().toString(),"","")
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
                messageBox.setText("")
            }
        }

        messageBox.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            else
                chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }

        //抓取SOS按鍵
        btn_sos = findViewById(R.id.btn_SOS)
        btn_sos.setOnClickListener {
            //可前往救援->map
            if(bo==1 && go_who!=FirebaseAuth.getInstance().currentUser!!.uid){
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://maps.google.com/maps?" +
                        "saddr=" + nowloc_latitude + "," + nowloc_longitude+"&"+
                        "daddr="+go_latitude+","+go_longitude)
                startActivity(intent)
            }
            //本身請求救援中->取消救援
            else if(bo==1 && go_who==FirebaseAuth.getInstance().currentUser!!.uid){
                val messageObj = Message("NO", FirebaseAuth.getInstance().currentUser!!.uid,gettime() , getBatteryLevel().toString(),nowloc_latitude,nowloc_longitude)
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
            }
            //空閒->請求救援
            else{
                val messageObj = Message("YES", FirebaseAuth.getInstance().currentUser!!.uid, gettime() , getBatteryLevel().toString(),nowloc_latitude,nowloc_longitude)
                mDbRef.child("chats").child("messages").push()
                    .setValue(messageObj)
            }
        }

        //可回傳值的activity，取代startActivity(無法回傳)及startActivityForResult(已棄用)
        //回傳picture選擇的NUMBER值及動作
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (RESULT_OK == activityResult.resultCode) {
                    val numberval : Int? = activityResult.data?.getIntExtra(
                        ChoicepicActivity.NUMBER ,-1)
                    if(numberval != -1){
                        val messageObj = Message(message=numberval.toString(), senderId = FirebaseAuth.getInstance().currentUser!!.uid , time = gettime() ,battery = getBatteryLevel().toString(),"","pic")
                        mDbRef.child("chats").child("messages").push()
                            .setValue(messageObj)
                    }
                }
            }

        //抓取貼圖選擇按鍵
        btn_pic = findViewById(R.id.sentStick)
        btn_pic.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, ChoicepicActivity::class.java)
            resultLauncher.launch(intent)
        }

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //抓取dataBase資訊
        mDbRef.child("chats").child("messages").orderByKey().limitToLast(30).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()
                for(postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)

                    messageList.add(message!!)
                }


                updateMsgUI()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        chatRecyclerView.postDelayed({
            chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }, 1000)
    }

    //請求位置權限
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

    //取得位置
    private fun initLoc() {
        locmgr = getSystemService(LOCATION_SERVICE) as
                LocationManager

        var loc: Location? = null
        try {
            loc = locmgr.getLastKnownLocation(
                LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
        }

        onLocationChanged(loc!!)

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
        nowloc_latitude = loc.latitude.toString()
        nowloc_longitude =loc.longitude.toString()
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    //取得時間
    private fun gettime():String{
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        val mon: String = getzero(c.get(Calendar.MONTH)+1)
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

    //更新訊息版面
    private fun updateMsgUI(){
        go_latitude = null
        go_longitude = null
        go_who = null
        bo = 0

        //判斷是否有行充請求
        val msgTop = messageList[messageList.size - 1]
        if (msgTop.latitude != null) {
            if(msgTop.message=="YES"){
                go_latitude = msgTop.latitude
                go_longitude = msgTop.longitude
                go_who = msgTop.senderId
                bo = 1
            }
        }


        //根據行動電源需求，更改SOS按鈕顯示
        if(bo==1){
            if(go_who==FirebaseAuth.getInstance().currentUser!!.uid){
                btn_sos.setBackgroundResource(R.drawable.button_sos_1)
                btn_sos.text = "等待救援"

            }
            else{
                btn_sos.setBackgroundResource(R.drawable.button_sos_1)
                btn_sos.text = "前往救援"

            }
        }
        else{
            btn_sos.setBackgroundResource(R.drawable.button_sos_0)
            btn_sos.text = "徵求行動電源SOS"
        }

        messageAdapter.notifyDataSetChanged()
    }

    //取得電量
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

}

