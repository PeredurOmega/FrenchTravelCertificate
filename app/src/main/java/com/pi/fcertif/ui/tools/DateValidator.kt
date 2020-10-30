package com.pi.fcertif.ui.tools

import java.util.*

/**
 * This interface is used to check if a date is valid.
 */
interface DateValidator {

    /**
     * Returns whether or not this date is valid.
     * @return [Int] Null if the date is valid or the int pointing to the string resource to use to
     * display the error.
     */
    fun isValid(calendar: Calendar): Int?
}
