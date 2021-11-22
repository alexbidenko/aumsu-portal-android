package admire.aumsu.portal.application.ui.news

import admire.aumsu.portal.application.models.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    var isDataRequested = false
    var messages = ArrayList<Message>()
}