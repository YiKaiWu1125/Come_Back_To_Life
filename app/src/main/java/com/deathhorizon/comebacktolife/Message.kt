package com.deathhorizon.comebacktolife

class Message {
    var message: String = ""
    var senderId: String? = null
    var time: String? = ""
    var latitude: String? = null
    var longitude: String? = null
    constructor(){}
    constructor(message: String , senderId: String?, time:String,latitude:String?,longitude:String?){
        this.message = message
        this.senderId = senderId
        this.time = time
        if(latitude!=""){
            this.latitude = latitude
        }
        if(longitude!=""){
            this.longitude = longitude
        }

    }
}