package com.example.cars.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cars.Car

@Database(entities = [ Car::class ], version=1, exportSchema = false)
@TypeConverters(CarTypeConverters::class)
abstract class CarDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
}