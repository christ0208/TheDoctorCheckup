package edu.bluejack181.thedoctorcheckups

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ForumTabAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private var mFragmentList: ArrayList<Fragment> = ArrayList()
    private var mFragmentListTitle: ArrayList<String> = ArrayList()

    override fun getItem(p0: Int): Fragment {
        return mFragmentList.get(p0)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String){
        mFragmentList.add(fragment)
        mFragmentListTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentListTitle.get(position)
    }
}