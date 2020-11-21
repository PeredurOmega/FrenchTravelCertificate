package com.pi.fcertif.ui.profiles

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R
import com.pi.fcertif.objects.UserInfo
import com.pi.fcertif.ui.home.ItemClickListener
import com.pi.fcertif.ui.home.SelectedListener
import com.pi.fcertif.ui.profile.ProfileFragment
import com.pi.fcertif.ui.tools.FlipAnimator


/**
 * [RecyclerView.ViewHolder] used to create profile "card" for the [RecyclerView] in
 * [ProfilesFragment].
 * @param itemView [View] to use to create this [RecyclerView.ViewHolder].
 * @param itemClickListener [ItemClickListener] to use to remove a profile through contextual
 * action bar.
 */
class ProfileViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView), SelectedListener {

    /**
     * [ImageView] where the profile icon is displayed.
     */
    private val iconView: ImageView = itemView.findViewById(R.id.icon)

    /**
     * [ImageView] where the icon indicating selection is displayed. Gone when not selected and
     * Visible when selected.
     */
    private val iconSelectedView: ImageView = itemView.findViewById(R.id.iconSelectedView)

    /**
     * Indicating whether or not this item is currently selected. Null means that this item has not
     * been bound.
     */
    private var selected: Boolean? = null

    /**
     * [TextView] where the short name of the bound [UserInfo] is displayed.
     */
    private val shortName: TextView = itemView.findViewById(R.id.shortName)

    /**
     * Binds the provided [UserInfo] to this [ProfileViewHolder].
     * @param userInfo [UserInfo] to bind to this [ProfileViewHolder].
     * @param position [Int] position of this item.
     * @param selectedNow [Boolean] True if the item is selected, false otherwise.
     */
    fun bindToProfile(userInfo: UserInfo, position: Int, selectedNow: Boolean) {
        val context = itemView.context

        when {
            selected == null -> unselected(false)
            selectedNow && selectedNow != selected -> selected(true)
            !selectedNow && selectedNow != selected -> unselected(true)
        }

        iconView.setImageDrawable(
            ContextCompat.getDrawable(context, R.drawable.profile_96px)
        )

        iconView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if(userInfo.id == "0") R.color.colorAccent else R.color.colorPrimary
            )
        )

        val short = "${userInfo.firstName} ${userInfo.lastName}"
        shortName.text = short

        itemView.setOnClickListener {
            if (!itemClickListener.selectedForActionMode(
                    position, this,
                    it.resources
                )
            ) {
                val navController = Navigation.findNavController((itemView.parent.parent) as View)
                println("PE WOW ${userInfo.id}")
                navController.navigate(
                    R.id.nav_profile,
                    bundleOf(ProfileFragment.PROFILE to userInfo.id)
                )
            }
        }

        itemView.setOnLongClickListener {
            itemClickListener.onLongClick(position, this, it.resources)
            return@setOnLongClickListener true
        }
    }

    override fun selected(animate: Boolean) {
        iconView.visibility = View.INVISIBLE
        iconSelectedView.visibility = View.VISIBLE
        iconSelectedView.alpha = 1f
        selected = true
        if (animate) FlipAnimator().flipView(iconSelectedView, iconView, true)
    }

    override fun unselected(animate: Boolean) {
        iconSelectedView.visibility = View.GONE
        iconView.visibility = View.VISIBLE
        iconView.alpha = 1f
        selected = false
        if (animate) FlipAnimator().flipView(iconSelectedView, iconView, false)
    }
}