package com.pi.fcertif.objects

import android.text.format.DateFormat
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility object that is used to store the date and the time in a good format.
 * @param date [String] date matching dd/mm/yyyy.
 * @param time [String] time matching HH:mm.
 */
class DateTime(val date: String, val time: String) : Serializable {

    /**
     * @return [String] representing the hours field of the time.
     */
    fun getHours(): String {
        return time.split(":")[0]
    }

    /**
     * @return [String] representing the minutes field of the time.
     */
    fun getMinutes(): String {
        return time.split(":")[1]
    }

    /**
     * Overrides toString() in order to provide a unique file name.
     */
    override fun toString(): String {
        return (date.plus("_").plus(time))
            .replace("/", "").replace(":", "")
    }

    /**
     * Checks if the [DateTime] is malformed.
     * @return True if the [DateTime] is malformed, false otherwise.
     */
    fun isMalformed(): Boolean {
        if (!date.contains("/")) return true
        if (!time.contains(":")) return true
        val dateFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "MM dd yyyy"),
            Locale.getDefault()
        )
        val timeFormat = SimpleDateFormat(
            DateFormat.getBestDateTimePattern(Locale.FRANCE, "HH mm"),
            Locale.getDefault()
        )
        try {
            dateFormat.parse(date)
            timeFormat.parse(time)
        } catch (e: Exception) {
            return true
        }
        val splitDate = date.split("/")
        if (splitDate.size != 3) return true

        val day = splitDate[0].toIntOrNull()
        val month = splitDate[1].toIntOrNull()

        if (day == null || day < 1 || day > 31) return true
        if (month == null || month < 1 || month > 12) return true

        val splitTime = time.split(":")
        if (splitTime.size != 2) return true

        val hours = splitTime[0].toIntOrNull()
        val minutes = splitTime[1].toIntOrNull()

        if (hours == null || hours < 0 || hours > 24) return true
        if (minutes == null || minutes < 0 || minutes > 60) return true

        return false
    }
}