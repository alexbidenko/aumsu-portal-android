package ru.aumsu.www.application.ui.committee

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_committee_result.view.*
import ru.aumsu.www.application.R


class CommitteeResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_committee_result, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.result_viewer.settings.javaScriptEnabled = true
        view.result_viewer.webViewClient = ResultWebViewClient()
        view.result_viewer.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + "http://www.aumsu.ru/images/PK/Priem_komis_page/Spiski_abitur/spiski_ratings/O4naya_budjet.pdf")
    }

    class ResultWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}
