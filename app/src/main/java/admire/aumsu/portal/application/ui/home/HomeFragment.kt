package admire.aumsu.portal.application.ui.home

import admire.aumsu.portal.application.BaseActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.view.*
import admire.aumsu.portal.application.R
import android.content.Intent
import android.net.Uri
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
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
        view.to_schedule.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aumsu-schedule.admire.social/?token=${BaseActivity.userData!!.token}"))
            startActivity(browserIntent)
        }
        view.to_vacancies.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://aumsuvacancy.ru.swtest.ru"))
            startActivity(browserIntent)
        }
    }
}