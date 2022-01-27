package com.aggarwalankur.hubsearch.view.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.aggarwalankur.hubsearch.databinding.FragmentDetailsBinding
import com.aggarwalankur.hubsearch.view.MainViewModel


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel :MainViewModel by activityViewModels()

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            user = args.selectedUser
        }

        //Set selected user in viewmodel
        viewModel.setSelectedUser(args.selectedUser)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    override fun onDestroyView() {
        //Clear selected user in viewmodel
        viewModel.setSelectedUser(null)
        super.onDestroyView()
    }

}