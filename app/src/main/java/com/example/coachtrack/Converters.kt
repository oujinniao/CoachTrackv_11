package com.example.coachtrack

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromEstadoPago(estadoPago: EstadoPago): String {
        return estadoPago.name
    }

    @TypeConverter
    fun toEstadoPago(estadoPago: String): EstadoPago {
        return EstadoPago.valueOf(estadoPago)
    }
}