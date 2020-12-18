package com.jjswigut.tender.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jjswigut.tender.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textSlideshow.text = it
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}