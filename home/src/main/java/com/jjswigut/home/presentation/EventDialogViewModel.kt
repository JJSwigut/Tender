package com.jjswigut.home.presentation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Group
import com.jjswigut.home.presentation.adapters.EventGroupAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventDialogViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {


    val groupSelection = MutableLiveData<String>().apply {
        value = "nobody"
    }
    val dateSelection = MutableLiveData<String>().apply {
        value = "the 1st of never"
    }

    var group: Group? = null


    fun getListOfGroups(adapter: EventGroupAdapter) {
        repo.getAllGroups()
            .addOnSuccessListener { result ->
                val groupList = arrayListOf<Group>()
                for (document in result) {
                    groupList.add(document.toObject())
                }
                adapter.updateData(groupList)
            }
            .addOnFailureListener {
                Log.d(
                    TAG,
                    "getListOfGroups: $it                                                          "
                )
            }
    }

    fun formatDate(date: Long): String {
        val formatter = SimpleDateFormat("EEE, MMM d, ''yy", Locale.US)
        return formatter.format(date)
    }


}
