package com.example.cars

import androidx.lifecycle.ViewModel
import kotlin.random.Random


class CarListViewModel : ViewModel() {

    private val carRepository = CarRepository.get()
    val carListLiveData = carRepository.getCars()

    fun addCar(car: Car) {
        carRepository.addCar(car)
    }

}