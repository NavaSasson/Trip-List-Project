package com.example.architectureproject.ui.image_labeling

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.architectureproject.R
import com.example.architectureproject.databinding.ImageLabelingLayoutBinding
import com.example.architectureproject.ui.TripsViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelingFragment : Fragment() {

    private var _binding: ImageLabelingLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripsViewModel by activityViewModels()

    private lateinit var imageLabeler: com.google.mlkit.vision.label.ImageLabeler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImageLabelingLayoutBinding.inflate(inflater, container, false)

        viewModel.imageLabeling.observe(viewLifecycleOwner) {
            binding.detectionText.text = it
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_objectDetectionFragment_to_detailItemFragment
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the image labeler
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f) // Set confidence threshold
            .build()

        imageLabeler = ImageLabeling.getClient(options)

        viewModel.chosenTrip.observe(viewLifecycleOwner) { trip ->
            if (trip.photo.isNullOrEmpty()) {
                viewModel.setDescription(getString(R.string.no_photo))
            } else {
                Glide.with(requireContext()).load(trip.photo).into(binding.tripImage)
                trip.photo?.let {
                    detectObjects(it)
                }
            }
        }
    }

    private fun detectObjects(photoUrl: String) {
        // Load the image as a Bitmap
        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val image = InputImage.fromBitmap(resource, 0)

                    imageLabeler.process(image)
                        .addOnSuccessListener { labels ->
                            handleDetectionSuccess(labels)
                        }
                        .addOnFailureListener { _ ->
                            viewModel.setImageLabeling(getString(R.string.image_labeling_failed))
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun handleDetectionSuccess(labels: List<ImageLabel>) {
        if (labels.isEmpty()) {
            viewModel.setImageLabeling(getString(R.string.no_labels_detected))
        }
        else {
            val labelsText = labels.joinToString("\n") { label ->
                label.text + getString(R.string.confidence) + "${label.confidence}"
            }
            viewModel.setImageLabeling(labelsText)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
