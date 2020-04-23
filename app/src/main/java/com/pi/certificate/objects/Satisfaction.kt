package com.pi.certificate.objects

import com.pi.certificate.R

/**
 * Enum used for satisfaction level of the user.
 */
enum class Satisfaction {
    /**
     * The user thinks it's an awesome app. 5 stars expected.
     */
    PERFECT,

    /**
     * The user thinks it could be a little bit better but he is still enjoying the app. 4-5 stars
     * expected.
     */
    GOOD,

    /**
     * The user thinks it could be much better but some features are still good. 3 stars expected.
     */
    AVERAGE,

    /**
     * The user thinks it's a bad app and it's really buggy or useless. 1-2 star(s) expected.
     */
    BAD,

    /**
     * The user thinks it's the worst app on earth. He doesn't even know why on earth it's still on
     * his phone. 1 start expected.
     */
    AWFUL;

    /**
     * Returns the refined value of this [Satisfaction].
     * @return [Int] pointing to the string text corresponding to this [Satisfaction].
     */
    fun refinedValue(): Int {
        return when(this){
            PERFECT -> R.string.very_satisfied
            GOOD -> R.string.satisfied
            AVERAGE -> R.string.moderately_satisfied
            BAD -> R.string.unsatisfied
            AWFUL -> R.string.very_unsatisfied
        }
    }
}