package com.example.cars

import android.app.Application

class CarsApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        CarRepository.initialize(this)
    }
}