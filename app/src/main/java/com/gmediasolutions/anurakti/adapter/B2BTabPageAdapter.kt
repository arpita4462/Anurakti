package com.gmediasolutions.anurakti.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.gmediasolutions.anurakti.b2b.ProductFragment
import com.gmediasolutions.anurakti.b2b.PropertyFragment

class B2BTabPageAdapter(fm: FragmentManager, internal var mNumOfTabsjh: Int) : FragmentStatePagerAdapter(fm) {
    private val jhtabTitles = arrayOf("Products", "Property")

    override fun getPageTitle(position: Int): CharSequence? {
        return jhtabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return ProductFragment()
            }
            1 -> {
                return PropertyFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabsjh
    }
}