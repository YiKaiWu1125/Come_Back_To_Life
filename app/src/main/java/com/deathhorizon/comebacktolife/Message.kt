package com.deathhorizon.comebacktolife

class Message {
    var message: String = ""
    var senderId: String? = null
    var time: String? = ""
    var latitude: String? = null
    var longitude: String? = null
    var battery: String? = ""
    constructor(){}
    constructor(message: String , senderId: String?, time:String,battery: String?, latitude:String?,longitude:String?){
        this.message = message
        this.senderId = senderId
        this.time = time
        this.battery = battery
        if(latitude!=""){
            this.latitude = latitude
        }
        if(longitude!=""){
            this.longitude = longitude
        }

    }
    constructor(message: String , senderId: String?,  time:String, latitude:String?,longitude:String?){
        this.message = message
        this.senderId = senderId
        this.time = time
        this.battery = time.substring(time.indexOf("_電源:")+4, time.indexOf(".0%"))
        if(latitude!=""){
            this.latitude = latitude
        }
        if(longitude!=""){
            this.longitude = longitude
        }

    }
}