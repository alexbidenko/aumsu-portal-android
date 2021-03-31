package ru.aumsu.www.application.ui.committee

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.fragment_committee_about.*

import ru.aumsu.www.application.R

class CommitteeAboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_committee_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        committee_about.text = HtmlCompat.fromHtml(getString(R.string.committee_about), HtmlCompat.FROM_HTML_MODE_COMPACT)
        committee_about.isClickable = true
        committee_about.movementMethod = LinkMovementMethod.getInstance()
    }
}
