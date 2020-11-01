package com.pi.fcertif.objects

import java.io.Serializable

/**
 * Object used to represent an exit's reason.
 * @param shortName [String] Short name of the reason.
 * @param fullDescription [String] Full description of this reason (provided by the French Ministry
 * of Home Affairs).
 * @param iconName [String] Name of the icon of this reason.
 * @param color [Int] Color value of the icon's background tint of this reason.
 * @param id [Int] Position / Id of this reason.
 * @param value [String] Official value of this reason.
 */
class Reason(
    val shortName: String, val fullDescription: String, val iconName: String,
    val color: Int, val id: Int, val value: String
) : Serializable