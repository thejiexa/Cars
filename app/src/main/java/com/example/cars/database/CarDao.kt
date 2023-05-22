package com.example.cars.database

import androidx.lifecycle.LiveData
import com.example.cars.Car
import java.util.*
import androidx.room.*

@Dao
interface CarDao {
    @Query("SELECT * FROM car")
    fun getCars(): LiveData<List<Car>>

    @Query("SELECT * FROM car WHERE id=(:id)")
    fun getCar(id: UUID): LiveData<Car?>

    @Update
    fun updateCar(car: Car)

    @Insert
    fun addCar(car: Car)
}