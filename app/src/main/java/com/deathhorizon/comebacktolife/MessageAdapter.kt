package com.deathhorizon.comebacktolife

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deathhorizon.comebacktolife.UIUtil.dp2px
import com.deathhorizon.comebacktolife.UIUtil.getScreenWidth
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter(val context: ChatroomActivity, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;
    val ITEM_RECEIVE_STICK = 3
    val ITEM_SENT_STICK = 4;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1) {
            //inflate recieve
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            return ReceiveViewHolder(view)
        }
        else if(viewType == 2){
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_msg, parent, false)
            return SentViewHolder(view)
        }
        else if(viewType == 3){
//            inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_stick, parent, false)
            return ReceiveStickViewHolder(view)
        }
        else {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_stick, parent, false)
            return SentStickViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if( currentMessage.message!=null){
            //////////////////////SENT
            if(holder.javaClass == SentViewHolder::class.java){
                // do the stuff fro sent view holder
                val viewHolder = holder as SentViewHolder

                if(currentMessage.latitude == null)
                    holder.sentMessage?.text = currentMessage.message
                else if(currentMessage.message=="YES")
                    holder.sentMessage?.text = "已發出電源請求\n <再次點擊 即取消往救援.>"
                else{
                    holder.sentMessage?.text="我 已獲得行動電源."
                }


                if(currentMessage.battery != null){
                    holder.sentBattery?.text = "電量:" + currentMessage.battery+"%"
                }
                else if(currentMessage.time != null && currentMessage.time?.indexOf("_電源:")!= -1){
                    val time = currentMessage.time
                    holder.sentBattery?.text = "電量:"+ time?.substring(time?.indexOf("_電源:")?.plus(4) ?: 0 , time.indexOf("%")?.plus(1)?:0)
                }

                if( currentMessage.time != null){
                    if(currentMessage.time?.indexOf("_電源:")==-1){
                        holder.sentTime?.text = currentMessage.time
                    }
                    else{
                        holder.sentTime?.text = currentMessage.time?.indexOf("_電源:")
                            ?.let { currentMessage.time?.substring(0, it) }
                    }
                }
            ////////////////////RECEIVE
            }else if (holder.javaClass == ReceiveViewHolder::class.java){
                // do ... reveive view holder
                val viewHolder = holder as ReceiveViewHolder

                if(currentMessage.latitude == null)
                    holder.receiveMessage?.text = currentMessage.message
                else if(currentMessage.message=="YES")

                    holder.receiveMessage?.text = "匿名 已發出請求"+"\n"+"<點擊 前往救援 即可前往救援.>"
                else{
                    holder.receiveMessage?.text="匿名 已獲得行動電源."
                }


                if(currentMessage.battery != null && currentMessage.battery != ""){
                    holder.receiveBattery?.text = "電量:" + currentMessage.battery+"%"
                }
                else if(currentMessage.time != null){
                    val time = currentMessage.time
                    if( currentMessage.time!!.indexOf("_電量:")!=-1)
                    holder.receiveBattery?.text = "電量:" + currentMessage.time!!.substring( currentMessage.time?.indexOf("_電量:")?.plus(4)?:0  ) + "%"
                    else holder.receiveBattery?.text = "電量: 0.0%"
                }

                if( currentMessage.time != null){
                    if(currentMessage.time?.indexOf("_電源:")==-1){
                        holder.receiveTime?.text = currentMessage.time
                    }
                    else{
                        holder.receiveTime?.text = currentMessage.time?.indexOf("_電源:")
                            ?.let { currentMessage.time?.substring(0, it) }
                    }
                }
            }else if(holder.javaClass == SentStickViewHolder::class.java){
                val viewHolder = holder as SentStickViewHolder
                val imgarray = arrayOf(
                    R.drawable.morning,
                    R.drawable.afternoon,
                    R.drawable.goodnight,
                    R.drawable.oh
                )
                val padding = 15
                val imageWidth: Int = getScreenWidth(this.context) / 10 * 6
                val params = LinearLayout.LayoutParams(imageWidth, imageWidth)

                holder.setSticks?.layoutParams = params


                holder.setSticks?.setImageResource(imgarray[currentMessage.message.toInt()])

                if(currentMessage.battery != null){
                    holder.sentBattery?.text = "電量:" + currentMessage.battery+"%"
                }
                else if(currentMessage.time != null && currentMessage.time?.indexOf("_電源:")!= -1){
                    val time = currentMessage.time
                    holder.sentBattery?.text = "電量:"+ time?.substring(time?.indexOf("_電源:")?.plus(4) ?: 0 , time.indexOf("%")?.plus(1)?:0)
                }
                if( currentMessage.time != null){
                    if(currentMessage.time?.indexOf("_電源:")==-1){
                        holder.sentTime?.text = currentMessage.time
                    }
                    else{
                        holder.sentTime?.text = currentMessage.time?.indexOf("_電源:")
                            ?.let { currentMessage.time?.substring(0, it) }
                    }
                }
            } else {
                val viewHolder = holder as ReceiveStickViewHolder

                val imgarray = arrayOf(
                    R.drawable.morning,
                    R.drawable.afternoon,
                    R.drawable.goodnight,
                    R.drawable.oh
                )
                val imageWidth: Int = getScreenWidth(this.context) / 10 * 6
                val params = LinearLayout.LayoutParams(imageWidth, imageWidth)
                holder.receiveSticks?.layoutParams = params


                holder.receiveSticks?.setImageResource(imgarray[currentMessage.message.toInt()])

                if(currentMessage.battery != null && currentMessage.battery != ""){
                    holder.receiveBattery?.text = "電量:" + currentMessage.battery+"%"
                }
                else if(currentMessage.time != null){
                    val time = currentMessage.time
                    if( currentMessage.time!!.indexOf("_電量:")!=-1)
                        holder.receiveBattery?.text = "電量:" + currentMessage.time!!.substring( currentMessage.time?.indexOf("_電量:")?.plus(4)?:0  ) + "%"
                    else holder.receiveBattery?.text = "電量: 0.0%"
                }

                if( currentMessage.time != null){
                    if(currentMessage.time?.indexOf("_電源:")==-1){
                        holder.receiveTime?.text = currentMessage.time
                    }
                    else{
                        holder.receiveTime?.text = currentMessage.time?.indexOf("_電源:")
                            ?.let { currentMessage.time?.substring(0, it) }
                    }
                }
            }

        }


    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            if(currentMessage.longitude == "pic") return ITEM_SENT_STICK
            return ITEM_SENT
        } else {
            if(currentMessage.longitude == "pic") return ITEM_RECEIVE_STICK
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }



    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView? = itemView.findViewById<TextView>(R.id.txt_sent_msgs)
        val sentBattery: TextView? = itemView.findViewById<TextView>(R.id.battery_sent_msgs)
        val sentTime   : TextView? = itemView.findViewById(R.id.time_sent_msgs)
    }
    class SentStickViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentTime   : TextView? = itemView.findViewById(R.id.time_sent_sticks)
        val sentBattery: TextView? = itemView.findViewById(R.id.battery_sent_sticks)
        val setSticks  : ImageView?= itemView.findViewById(R.id.img_sent_sticks)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView? = itemView.findViewById<TextView>(R.id.txt_receive_msgs)
        val receiveBattery: TextView? = itemView.findViewById<TextView>(R.id.battery_receive_msgs)
        val receiveTime   : TextView? = itemView.findViewById(R.id.time_receive_msgs)
    }
    class ReceiveStickViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveTime   : TextView? = itemView.findViewById(R.id.time_receive_sticks)
        val receiveBattery: TextView? = itemView.findViewById(R.id.battery_receive_sticks)
        val receiveSticks  : ImageView?= itemView.findViewById(R.id.img_receive_sticks)
    }
}