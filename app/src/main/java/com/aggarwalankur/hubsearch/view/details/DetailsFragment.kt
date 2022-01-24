package com.aggarwalankur.hubsearch.view.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.FragmentDetailsBinding
import com.aggarwalankur.hubsearch.view.MainViewModel


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

        val args = DetailsFragmentArgs.fromBundle(requireArguments())

        Toast.makeText(activity, "Name = ${args.selectedUser.login}", Toast.LENGTH_SHORT).show()

        binding.user = args.selectedUser

        binding.viewModel = viewModel

        return binding.root
    }

}