package com.logomann.datascanner20.di


import com.logomann.datascanner20.ui.authorization.view_model.AuthorizationViewModel
import com.logomann.datascanner20.ui.camera.view_model.CameraViewModel
import com.logomann.datascanner20.ui.car.view_model.CarLotInLadungViewModel
import com.logomann.datascanner20.ui.car.view_model.CarLottingViewModel
import com.logomann.datascanner20.ui.car.view_model.CarRelocationViewModel
import com.logomann.datascanner20.ui.car.view_model.CarSearchByPlaceViewModel
import com.logomann.datascanner20.ui.car.view_model.CarSearchByVINViewModel
import com.logomann.datascanner20.ui.container.view_model.ContainerArrivalViewModel
import com.logomann.datascanner20.ui.container.view_model.ContainerInCarriageViewModel
import com.logomann.datascanner20.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { AuthorizationViewModel(get()) }
    viewModel {
        SettingsViewModel(get())
    }
    viewModel {
        CarSearchByVINViewModel(get())
    }
    viewModel {
        CameraViewModel()
    }
    viewModel {
        CarRelocationViewModel(get())
    }
    viewModel {
        CarSearchByPlaceViewModel(get())
    }

    viewModel {
        CarLotInLadungViewModel(get())
    }
    viewModel {
        ContainerArrivalViewModel(get())
    }
    viewModel {
        ContainerInCarriageViewModel(get())
    }
    viewModel {
        CarLottingViewModel(get())
    }
}