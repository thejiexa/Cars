package com.example.cars

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Car (@PrimaryKey
                val id: UUID = UUID.randomUUID(),
                var mark: String = "",
                var model: String = "",
                var year: Int = 0,
                var price: Int = 0)