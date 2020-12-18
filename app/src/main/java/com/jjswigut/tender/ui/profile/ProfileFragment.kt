package com.jjswigut.tender.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jjswigut.tender.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textGallery.text = it
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}