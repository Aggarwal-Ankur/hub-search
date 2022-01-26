package com.aggarwalankur.hubsearch.view.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.FragmentDetailsBinding
import com.aggarwalankur.hubsearch.view.MainViewModel


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val args = DetailsFragmentArgs.fromBundle(requireArguments())

        binding.user = args.selectedUser
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //Set selected user in viewmodel
        viewModel.setSelectedUser(args.selectedUser)

        return binding.root
    }

    override fun onDestroyView() {
        //Clear selected user in viewmodel
        viewModel.setSelectedUser(null)
        super.onDestroyView()
    }

}