package com.example.coachtrack

import  androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "profesores",
    indices = [
        Index(value = ["firebaseId"], unique = true),
        Index(value = ["email"], unique = true)]  //Bloquea profesores duplicados

    )
data class ProfesorEntity(
    @PrimaryKey(autoGenerate = true)
    val id:  Long=0L,
    val firebaseId:String?=null,
    val nombre: String,
    val especialidad: String,
    val telefono: String,
    val email: String,
    val descripcion: String,
    val disponibilidad: String
)