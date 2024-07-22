package com.example.architectureproject.ui.single_character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.architectureproject.R
import com.example.architectureproject.databinding.DetailItemLayoutBinding
import com.example.architectureproject.ui.TripsViewModel

class DetailItemFragment :Fragment() {

    private var _binding : DetailItemLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel : TripsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailItemLayoutBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailItemFragment_to_allItemsFragments
            )
        }

        binding.aiBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailItemFragment_to_objectDetectionFragment
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.chosenTrip.observe(viewLifecycleOwner){

            when
            {
                it.title.isEmpty() -> binding.tripTitle.text = getString(R.string.no_trip_title)
                it.title.isNotEmpty() -> binding.tripTitle.text = it.title
            }

            when
            {
                it.description.isEmpty() -> binding.tripDescription.text = getString(R.string.no_trip_description)
                it.description.isNotEmpty() -> binding.tripDescription.text = it.description
            }

            when
            {
                it.location.isEmpty() -> binding.tripLocation.text = getString(R.string.no_location)
                it.location.isNotEmpty() -> binding.tripLocation.text = it.location
            }

            Glide.with(requireContext()).load(it.photo).into(binding.tripImage)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}