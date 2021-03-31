package ru.aumsu.www.application.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.view.*
import ru.aumsu.www.application.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.to_gallery.setOnClickListener {
            findNavController().navigate(R.id.nav_gallery)
        }
        view.to_news.setOnClickListener {
            findNavController().navigate(R.id.nav_slideshow)
        }
        view.to_committee.setOnClickListener {
            findNavController().navigate(R.id.nav_tools)
        }
        view.to_about.setOnClickListener {
            findNavController().navigate(R.id.nav_share)
        }
    }
}