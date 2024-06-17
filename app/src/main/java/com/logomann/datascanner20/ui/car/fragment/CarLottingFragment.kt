package com.logomann.datascanner20.ui.car.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logomann.datascanner20.R
import com.logomann.datascanner20.databinding.FragmentCarLottingBinding
import com.logomann.datascanner20.ui.ScreenState
import com.logomann.datascanner20.ui.camera.fragment.CameraFragment
import com.logomann.datascanner20.ui.car.DriverAdapter
import com.logomann.datascanner20.ui.car.view_model.CarLottingViewModel
import com.logomann.datascanner20.ui.snackbar.SnackbarMessage
import com.logomann.datascanner20.util.CAMERA_REQUEST_KEY
import com.logomann.datascanner20.util.CAMERA_RESULT
import org.koin.androidx.viewmodel.ext.android.viewModel

class CarLottingFragment : Fragment() {
    private var _binding: FragmentCarLottingBinding? = null
    private val binding get() = _binding!!
    private lateinit var vinCode: EditText
    private lateinit var driver: EditText
    private lateinit var recyclerView: RecyclerView
    private var listOfCars = mutableListOf<String>()

    private val adapter = DriverAdapter(listOfCars) {
        setOnItemAction(it)
    }
    private val viewModel by viewModel<CarLottingViewModel>()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarLottingBinding.inflate(inflater, container, false)
        vinCode = binding.carLottingVinCodeEt
        driver = binding.carLottingDriverEt
        recyclerView = binding.carLottingRecycler
        binding.carLottingCamera.setOnClickListener {
            startCamera()
        }
        binding.carLottingBtnClear.setOnClickListener {
            clearText()
        }
        binding.carLottingAddBtn.setOnClickListener {
            if (checkAddToList()) {
                viewModel.addToList(vinCode.text.toString())
            }
        }
        binding.carLottingOkBtn.setOnClickListener {
            if (checkRequest()) {
                hideKeyboard()
                viewModel.request(driver.text.toString())
            }
        }

        val driverRecyclerView = binding.carLottingRecycler
        driverRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        driverRecyclerView.adapter = adapter
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.CameraResult -> {
                    vinCode.setText(screenState.result)
                }

                is ScreenState.Content -> {
                    showGroup()
                    showMessage(screenState.message.toString(),false)
                    clearText()
                    viewModel.clearList()
                }

                ScreenState.Default -> {}
                is ScreenState.Error -> {
                    showGroup()
                    showCreateLotBtn()
                    showMessage(screenState.message.toString(),true)
                }

                ScreenState.Loading -> {
                    hideKeyboard()
                    hideGroup()
                }

                ScreenState.NoInternet -> {
                    showGroup()
                    showCreateLotBtn()
                    showMessage(getString(R.string.nointernet),true)
                }

                ScreenState.ServerError -> {
                    showGroup()
                    showCreateLotBtn()
                    showMessage(getString(R.string.server_error),true)
                }

                is ScreenState.ListRefreshed -> {
                    listOfCars.clear()
                    listOfCars.addAll(screenState.list)
                    if (listOfCars.isEmpty()) {
                        hideCreateBtn()
                    } else {
                        showCreateLotBtn()
                    }
                    adapter.notifyDataSetChanged()
                }

                is ScreenState.AddressCleared -> {}
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CAMERA_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setCameraResult(bundle.getString(CAMERA_RESULT).toString())
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun startCamera() {
        requireActivity().supportFragmentManager.commit {
            add(R.id.fragment_container_view, CameraFragment())
            addToBackStack(null)
        }
    }

    private fun showGroup() {
        binding.carLottingPb.isVisible = false
        binding.carRelocationGroup.isVisible = true
    }

    private fun hideGroup() {
        binding.carRelocationGroup.isVisible = false
        binding.carLottingOkBtn.isVisible = false
        binding.carLottingPb.isVisible = true

    }

    private fun showCreateLotBtn() {
        binding.carLottingOkBtn.isVisible = true
    }

    private fun hideCreateBtn() {
        binding.carLottingOkBtn.isVisible = false
    }

    private fun showMessage(message: String, isError: Boolean) {
        if (isError) {
            SnackbarMessage.showMessageError(binding.cl, message,requireContext())
        } else {
            SnackbarMessage.showMessageOk(binding.cl, message,requireContext())
        }
    }

    private fun clearText() {
        vinCode.setText(getString(R.string.empty))
        driver.setText(getString(R.string.empty))
        viewModel.clearList()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(vinCode.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setOnItemAction(vin: String) {
        viewModel.removeFromList(vin)
    }

    private fun checkRequest(): Boolean {
        if (!driver.text.isNullOrEmpty() && listOfCars.isNotEmpty()) {
            return true
        } else {
            showMessage(getString(R.string.not_all_cells_are_filled),true)
            return false
        }
    }
    private fun checkAddToList(): Boolean {
        if (vinCode.text.length < 17) {
            showMessage(getString(R.string.vin_is_incorrect), true)
            return false
        } else if (listOfCars.contains(vinCode.text.toString())){
            showMessage(getString(R.string.vin_already_in_list),true)
            return false
        } else {
            return true
        }
    }
}