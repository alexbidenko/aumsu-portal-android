package ru.aumsu.www.application.ui.committee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_committee.*
import ru.aumsu.www.application.R

class CommitteeFragment : Fragment() {

    private lateinit var committeeViewModel: CommitteeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        committeeViewModel =
            ViewModelProviders.of(this).get(CommitteeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_committee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = CommitteeFragmentPagerAdapter(childFragmentManager)
        pager_title.setupWithViewPager(pager)
    }

    private class CommitteeFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CommitteeAboutFragment()
                1 -> CommitteeResultFragment()
                else -> CommitteeAboutFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "О комиссии"
                1 -> "Результаты"
                else -> ""
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }
}