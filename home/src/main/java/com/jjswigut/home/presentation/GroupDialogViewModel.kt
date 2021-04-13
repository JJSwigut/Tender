package com.jjswigut.home.presentation

import android.content.Context
import android.text.Editable
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import com.jjswigut.home.R
import com.jjswigut.home.databinding.FragmentGroupDialogueBinding
import com.jjswigut.home.presentation.adapters.GroupAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupDialogViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {

    var isModifyDialog: Boolean = false

    private val userList = arrayListOf<User>()

    val newGroupUserList = arrayListOf<User>()

    private val groupUserList = arrayListOf<User>()

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


    fun getListOfAllUsers(adapter: GroupAdapter) {
        repo.getAllUsers()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    userList.add(document.toObject())
                }
                adapter.updateData(userList)
            }
    }

    fun updateGroup(name: String) {
        val modifiedGroup = hashMapOf(
            "groupName" to name,
            "users" to groupUserList
        )
        group?.groupId?.let {
            repo.getGroup(it).addOnSuccessListener { group ->
                group.reference.set(modifiedGroup, SetOptions.merge())
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
    }

    fun deleteGroup(context: Context) {
        group?.groupId?.let {
            repo.deleteGroup(it).addOnSuccessListener {
                toast(
                    context.getString(R.string.delete_group_toast),
                    context
                )
            }
                .addOnFailureListener { toast(context.getString(R.string.failure_toast), context) }
        }

    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun toast(toast: String, context: Context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }
}
