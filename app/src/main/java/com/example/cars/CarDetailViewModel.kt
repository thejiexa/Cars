package com.example.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*

class CarDetailViewModel() : ViewModel() {


    private val carRepository = CarRepository.get()
    private val carIdLiveData = MutableLiveData<UUID>()
    var carLiveData: LiveData<Car?> =
        Transformations.switchMap(carIdLiveData) { carId ->
            carRepository.getCar(carId)
        }

    fun loadCar(carId: UUID) {
        carIdLiveData.value = carId
    }

    fun saveCar(car: Car) {
        carRepository.updateCar(car)
    }
}