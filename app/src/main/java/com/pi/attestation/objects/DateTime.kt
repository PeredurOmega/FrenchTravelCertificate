package com.pi.attestation.objects

import java.io.Serializable

class DateTime(val date: String, val time: String) : Serializable{
    fun getHours(): String{
        return time.split(":")[0]
    }

    fun getMinutes(): String{
        return time.split(":")[1]
    }

    override fun toString(): String {
        return (date.plus("_").plus(time))
            .replace("/", "").replace(":", "")
    }
}