package com.example.architectureproject.ui.all_characters

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architectureproject.R
import com.example.architectureproject.data.models.Trip
import com.example.architectureproject.databinding.AllItemsLayoutBinding
import com.example.architectureproject.ui.TripsViewModel

class AllItemsFragments : Fragment(){

    private var _binding : AllItemsLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel : TripsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AllItemsLayoutBinding.inflate(inflater, container, false)

        binding.addBtn.setOnClickListener{

            findNavController().navigate(R.id.action_allItemsFragments_to_addItemFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        arguments?.getString("title")?.let {
//            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
//        }

        viewModel.trips?.observe(viewLifecycleOwner){

            binding.recycler.adapter = TripAdapter(it, object : TripAdapter.TripListener {
                override fun onTripClick(index: Int) {
                    val trip = it[index]
                    viewModel.setTrip(trip)
                    findNavController().navigate(R.id.action_allItemsFragments_to_detailItemFragment)
                }

                override fun onTripLongClick(index: Int) {}

                override fun onEditButtonClick(trip: Trip) {
                    viewModel.setTrip(trip)
                    findNavController().navigate(R.id.action_allItemsFragments_to_editTripFragment)
                }

                override fun onDeleteButtonClick(trip: Trip) {
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.confirm_delete))
                        .setMessage(getString(R.string.sure_you_want_to_delete))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.deleteTrip(trip)
                        }
                        .setNegativeButton(getString(R.string.no), null)
                        .create()

                    alertDialog.setOnShowListener {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_green))
                    }

                    alertDialog.show()
                }

            })

            binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        }


        ItemTouchHelper(object : ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
                //we can to do makeMovementFlags instead of makeFlag and its gives ua all the directions
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            //dragging -up or down
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false


            //swipe - left or right
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
           }
        }).attachToRecyclerView(binding.recycler)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}