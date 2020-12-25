package com.diaz.project.casher.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.diaz.project.casher.HutangFragment
import com.diaz.project.casher.RiwayatFragment

class PagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragments = listOf(RiwayatFragment(), HutangFragment())

    override fun getItem(p0: Int): Fragment {
        return fragments[p0]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "History"
            1 -> "Loan"
            else -> super.getPageTitle(position)
        }
    }
}