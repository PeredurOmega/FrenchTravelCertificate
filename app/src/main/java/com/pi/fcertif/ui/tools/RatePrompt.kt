package com.pi.fcertif.ui.tools

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.pi.fcertif.R
import com.pi.fcertif.objects.Rate
import com.pi.fcertif.objects.Satisfaction

/**
 * Utility class used to prompt the user in case he has already used the app (three certificates in
 * stocks) and in case he has not already seen this prompt asking for rating.
 * @param context [FragmentActivity] used to prompt the user and to retrieve its shared preferences.
 */
class RatePrompt(val context: FragmentActivity) {

    /**
     * Checks whether or not the user has already been prompted to rate the app. If he hasn't we ask
     * him to rate the app, else we do nothing.
     */
    fun promptIfNeeded(){
        Thread {
            val resources = context.resources
            val sharedPref = context.getSharedPreferences(
                resources.getString(R.string.shared_pref),
                Context.MODE_PRIVATE
            )
            val alreadyRated = sharedPref.getBoolean(
                resources.getString(R.string.already_rated),
                false
            )
            if (!alreadyRated) context.runOnUiThread { prompt(resources, sharedPref) }
        }.start()
    }

    /**
     * Prompts the user to rate the app.
     * @param resources [Resources] to use to retrieve texts (for internationalization) and
     * drawables.
     * @param sharedPref [SharedPreferences] to which we should apply the fact that the user has
     * already been prompted.
     */
    private fun prompt(resources: Resources, sharedPref: SharedPreferences) {
        val editor = sharedPref.edit()
        editor.putBoolean(resources.getString(R.string.already_rated), true)
        editor.apply()

        val rates: Array<Rate> = arrayOf(Rate(Satisfaction.PERFECT, R.drawable.perfect_icon),
            Rate(Satisfaction.GOOD, R.drawable.good_icon),
            Rate(Satisfaction.AVERAGE, R.drawable.average_icon),
            Rate(Satisfaction.BAD, R.drawable.bad_icon),
            Rate(Satisfaction.AWFUL, R.drawable.awful_icon))

        val adapter: ListAdapter = object : ArrayAdapter<Rate?>(context,
            R.layout.dialog_item, R.id.itemTextView, rates) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val tv = v.findViewById<View>(R.id.itemTextView) as TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(rates[position].icon, 0, 0,
                    0)
                tv.compoundDrawablePadding = ((25 * resources.displayMetrics.density).toInt())
                tv.setText(rates[position].satisfaction.refinedValue())
                return v
            }
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.are_you_satisfied)
            .setAdapter(adapter) { _, which ->
                val starCount = (5 - which)
                val params = Bundle()
                params.putInt("rate", starCount)
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                firebaseAnalytics.logEvent("rate_app", params)
                if(starCount == 5 || which == 4){
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.review_the_app)
                        .setMessage(R.string.help_us_by_reviewing)
                        .setPositiveButton(R.string.rate) { _, _ ->
                            val shortenLink = "https://play.google.com/store/" +
                                    "apps/details?id=com.pi.fcertif"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(shortenLink)
                            context.startActivity(i)

                            val params2 = Bundle()
                            params2.putInt("rate", starCount)
                            firebaseAnalytics.logEvent("rate_on_store", params2)
                        }
                        .setNegativeButton(R.string.do_not_rate) { _, _ ->
                            val params2 = Bundle()
                            params2.putInt("rate", starCount)
                            firebaseAnalytics.logEvent("do_not_rate_on_store", params2)
                        }
                        .setCancelable(false)
                        .show()
                }else{
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.send_feedback)
                        .setMessage(R.string.your_feedback_matters)
                        .setPositiveButton(R.string.send_email) { _, _ ->
                            val intent = Intent(Intent.ACTION_SENDTO)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_EMAIL, resources.getString(R.string.email))
                            intent.putExtra(Intent.EXTRA_SUBJECT,
                                resources.getString(R.string.email_feedback_subject))
                            intent.putExtra(Intent.EXTRA_TEXT,
                                resources.getString(R.string.email_feedback_body))

                            context.startActivity(Intent.createChooser(intent,
                                resources.getString(R.string.send_email)))

                            val params2 = Bundle()
                            params2.putBoolean("sent", true)
                            firebaseAnalytics.logEvent("feedback", params2)
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                            val params2 = Bundle()
                            params2.putBoolean("sent", false)
                            firebaseAnalytics.logEvent("feedback", params2)
                        }
                        .setCancelable(false)
                        .show()
                }
            }
            .setCancelable(false)
            .show()
    }
}