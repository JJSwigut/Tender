package com.jjswigut.home.presentation

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.core.utils.State
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.MinimalUser
import com.jjswigut.data.models.User
import com.jjswigut.home.R
import com.jjswigut.home.databinding.FragmentGroupDialogueBinding
import com.jjswigut.home.presentation.adapters.GroupAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class GroupDialogViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {

    var isModifyDialog: Boolean = false

    val newGroupUserList = arrayListOf<MinimalUser>()

    private val groupUserList = arrayListOf<MinimalUser>()

    private var group: Group? = null


    fun getGroupToModify(
        adapter: GroupAdapter,
        groupId: String,
        binding: FragmentGroupDialogueBinding
    ) {
        repo.getGroup(groupId)
            .addOnSuccessListener {
                groupUserList.clear()
                group = it.toObject()
                for (user in group?.users!!) {
                    groupUserList.add(user)
                }
                adapter.updateData(groupUserList)
                binding.groupNameInputTextView.text = group!!.groupName?.toEditable()
            }
    }

    val listOfAllUsers = liveData(Dispatchers.IO) {
        emit(State.Loading)
        try {
            repo.getAllUsers().collect { users ->
                val listOfMinimalUsers = users.map { user: User ->
                    MinimalUser(
                        user.userId,
                        user.profilePhotoUrl,
                        user.name
                    )
                }
                emit(State.Success(listOfMinimalUsers))
            }
        } catch (exception: Exception) {
            emit(State.Failed(exception.toString()))
            Log.d(ContentValues.TAG, ":${exception.message} ")
        }
    }


    fun updateGroup(name: String) {
        val modifiedGroup = hashMapOf(
            "groupId" to group?.groupId,
            "groupName" to name,
            "users" to groupUserList
        )
        group?.groupId?.let {
            repo.getGroup(it).addOnSuccessListener { group ->
                group.reference.set(modifiedGroup, SetOptions.merge())
            }
        }
        groupUserList.listIterator().forEach { user ->
            user.userId?.let { id ->
                val userRef = repo.getUserReference(id)
                userRef.update(
                    "userGroups",
                    FieldValue.arrayRemove(group)
                )
                userRef.update(
                    "userGroups",
                    FieldValue.arrayUnion(modifiedGroup)
                )
            }

        }
    }


    fun createNewGroup(name: String) {
        val newGroupRef = repo.createNewGroup()
        val newGroup = hashMapOf(
            "groupId" to newGroupRef.id,
            "groupName" to name,
            "users" to newGroupUserList
        )
        newGroupRef.set(newGroup, SetOptions.merge())

        newGroupUserList.listIterator().forEach { user ->
            user.userId?.let { id ->
                repo.getUserReference(id)
                    .update("userGroups", FieldValue.arrayUnion(newGroup))
                    .addOnFailureListener {
                        Log.d(TAG, "createNewGroup: group not shared")
                    }
            }
        }
    }


    fun deleteGroup(context: Context) {
        group?.groupId?.let {
            repo.deleteGroup(it).addOnSuccessListener {
                toast(
                    context.getString(R.string.delete_group_toast),
                    context
                )
            }
                .addOnFailureListener {
                    toast(
                        context.getString(R.string.failure_toast),
                        context
                    )
                }
        }
        groupUserList.listIterator().forEach { user ->
            user.userId?.let { id ->
                repo.getUserReference(id)
                    .update("userGroups", FieldValue.arrayRemove(group))
            }
        }

    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun toast(toast: String, context: Context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }
}
