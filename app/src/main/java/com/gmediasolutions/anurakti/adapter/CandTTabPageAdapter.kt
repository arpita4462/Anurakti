package com.gmediasolutions.anurakti.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.gmediasolutions.anurakti.careerandtalent.CompanyFragment
import com.gmediasolutions.anurakti.careerandtalent.StudentFragment

class CandTTabPageAdapter(fm: FragmentManager, internal var mNumOfTabscandt: Int) : FragmentStatePagerAdapter(fm) {
    private val candttabTitles = arrayOf("Student", "Company")

    override fun getPageTitle(position: Int): CharSequence? {
        return candttabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return StudentFragment()
            }
            1 -> {
                return CompanyFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabscandt
    }
}