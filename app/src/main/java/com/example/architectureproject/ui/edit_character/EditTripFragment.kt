package com.example.architectureproject.ui.edit_character

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.architectureproject.R
import com.example.architectureproject.databinding.DialogTitleWithCloseBinding
import com.example.architectureproject.databinding.EditTripLayoutBinding
import com.example.architectureproject.ui.TripsViewModel
import java.io.File

class EditTripFragment : Fragment() {

    private var _binding: EditTripLayoutBinding? = null
    private val binding get() = _binding!!

    private var _bindingDialog: DialogTitleWithCloseBinding? = null
    private val bindingDialog get() = _bindingDialog!!

    private lateinit var file: File

    private var fileUri: Uri? = null

    private val viewModel: TripsViewModel by activityViewModels()

    private val locationRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getLocationUpdates()
            }
        }

    private fun getLocationUpdates() {
        viewModel.location.observe(viewLifecycleOwner) {
            binding.itemLocation.setText(it)
            viewModel.setLocation(it)
        }
    }

    private val cameraFullSizeImageLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                viewModel.setImageUri(Uri.fromFile(file))
            }
        }

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.setImageUri(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditTripLayoutBinding.inflate(inflater, container, false)

        viewModel.imageUri.observe(viewLifecycleOwner) {
            it?.let { uri ->
                Glide.with(requireContext()).load(uri).into(binding.resultImage)
            }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            binding.itemTitle.setText(it)
        }

        viewModel.description.observe(viewLifecycleOwner) {
            binding.itemDescription.setText(it)
        }

        viewModel.locationVM.observe(viewLifecycleOwner) {
            binding.itemLocation.setText(it)
        }


        binding.backEditBtn?.setOnClickListener {
            showBackDialog()
        }

        binding.locationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getLocationUpdates()
                } else {
                    locationRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                viewModel.setLocation("")
                binding.itemLocation.setText("")
            }
        }


        binding.imageBtn.setOnClickListener {
            // Inflate the custom layout using view binding
            _bindingDialog = DialogTitleWithCloseBinding.inflate(layoutInflater)

            // Set the title text programmatically
            bindingDialog.dialogTitle.text = getString(R.string.change_image)

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCustomTitle(bindingDialog.root)

            builder.apply {
                setMessage(getString(R.string.from_where_upload_the_image))
                setCancelable(false)
                setIcon(R.drawable.baseline_exit_to_app_24)
                setPositiveButton(getString(R.string.camera)) { _, _ ->
                    file = File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "image.jpg"
                    )
                    viewModel.setImageUri(
                        FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.architectureproject.provider",
                            file
                        )
                    )
                    fileUri = FileProvider.getUriForFile(
                            requireContext(),
                    "com.example.architectureproject.provider",
                    file
                    )
                    cameraFullSizeImageLauncher.launch(viewModel.imageUri.value)
                }
                setNegativeButton(R.string.gallery) { _, _ ->
                    pickImageLauncher.launch(arrayOf("image/*"))
                }
            }

            val dialog = builder.create()

            // Set up the custom title view's close button
            bindingDialog.closeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
            }

            dialog.show()
        }


        return binding.root
    }


    private fun showBackDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exit_confirmation))
            .setMessage(getString(R.string.without_saving_your_changes))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.clearTemporaryData() // Clear temporary data when exiting without saving
                findNavController().navigate(R.id.action_editTripFragment_to_allItemsFragments)
            }
            .setNegativeButton(getString(R.string.no), null)
            .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
        }
        alertDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showBackDialog()
                }
            })

        viewModel.chosenTrip.observe(viewLifecycleOwner) { trip ->
            binding.itemTitle.setText(trip.title)
            binding.itemDescription.setText(trip.description)
            Glide.with(requireContext()).load(trip.photo).into(binding.resultImage)
            binding.itemLocation.setText(trip.location)


            binding.saveBtn.setOnClickListener {
                val newTitle = binding.itemTitle.text.toString()

                if (newTitle.isEmpty()) {
                    Toast.makeText(context, R.string.you_must_enter_a_title, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Check if the description is empty and set it to "No description" if it is
                    val newDescription = binding.itemDescription.text.toString().ifEmpty {
                        getString(R.string.no_description)
                    }

                    // Check if the location is empty and set it to "No location" if it is
                    val newLocation = binding.itemLocation.text.toString().ifEmpty {
                        getString(R.string.no_location)
                    }

                    val newPhoto = viewModel.imageUri.value?.toString() ?: trip.photo

                    // Save changes and navigate back
                    viewModel.updateTrip(trip, newTitle, newDescription, newPhoto, newLocation)
                    findNavController().navigate(
                        R.id.action_editTripFragment_to_allItemsFragments
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.setTitle(binding.itemTitle.text.toString())
        viewModel.setDescription(binding.itemDescription.text.toString())
        viewModel.setLocation(binding.itemLocation.text.toString())
        viewModel.setImageUri(viewModel.imageUri.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bindingDialog = null
    }
}