package com.gmediasolutions.anurakti.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.gmediasolutions.anurakti.socialmedia.*

class FriendsTabPageAdapter(fm: FragmentManager, internal var mNumOfTabsfrd: Int) : FragmentStatePagerAdapter(fm) {
    private val frndtabTitles = arrayOf("Friends List", "Friends Suggestion", "Friends Request", "Sent Request")

    override fun getPageTitle(position: Int): CharSequence? {
        return frndtabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return UserFriendsListFragment()
            }
            1 -> {
                return FriendSuggetionFragment()
            }
            2 -> {
                return UserGetFrndReqFragment()
            }
            3 -> {
                return UserSentFrndReqFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabsfrd
    }
}