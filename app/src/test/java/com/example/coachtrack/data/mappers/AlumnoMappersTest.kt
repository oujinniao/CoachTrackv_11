package com.example.coachtrack.data.mappers

import com.example.coachtrack.AlumnoEntity
import com.example.coachtrack.data.cloud.AlumnoCloud
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlumnoMapperTest {

    @Test
    fun `toCloud mantiene los campos basicos y asigna profesorId`() {
        val alumnoLocal = AlumnoEntity(
            localId = 1L,
            nombre = "Luis Rojas",
            nivelActual = "Intermedio",
            objetivo = "Mejorar saque",
            clasesPactadas = 10,
            clasesCursadas = 4,
            estadoPago = "PENDIENTE",
            telefono = "123456789",
            direccion = "Club Central",
            notasEntrenador = "Buen compromiso",
            edad = 15,
            profesorInstructor = null
        )

        val profesorId = "prof_123"

        val cloud = alumnoLocal.toCloud(profesorId)

        assertEquals("alumno1", cloud.id)
        assertEquals("Luis Rojas", cloud.nombre)
        assertEquals("Intermedio", cloud.nivelActual)
        assertEquals("Mejorar saque", cloud.objetivo)
        assertEquals(10, cloud.clasesPactadas)
        assertEquals(4, cloud.clasesCursadas)
        assertEquals("PENDIENTE", cloud.estadoPago)
        assertEquals("123456789", cloud.telefono)
        assertEquals("Club Central", cloud.direccion)
        assertEquals("Buen compromiso", cloud.notasEntrenador)
        assertEquals(15, cloud.edad)

        assertEquals(profesorId, cloud.profesorId)
        assertNull(cloud.fechaCreacion)
    }

    @Test
    fun `toLocal convierte correctamente desde AlumnoCloud`() {
        val alumnoCloud = AlumnoCloud(
            id = "alumno2",
            nombre = "Ana Pérez",
            nivelActual = "Avanzado",
            objetivo = "Fondo físico",
            clasesPactadas = 8,
            clasesCursadas = 3,
            estadoPago = "ADELANTADO",
            telefono = "987654321",
            direccion = "Academia Norte",
            notasEntrenador = "Golpes muy sólidos",
            edad = 18,
            profesorId = "prof_999",
            fechaCreacion = Timestamp.now(),
            fechaUltimaActualizacion = Timestamp.now()
        )

        val local = alumnoCloud.toLocal()

        assertEquals("alumno2", local.firebaseId)
        assertEquals("Ana Pérez", local.nombre)
        assertEquals("Avanzado", local.nivelActual)
        assertEquals("Fondo físico", local.objetivo)
        assertEquals(8, local.clasesPactadas)
        assertEquals(3, local.clasesCursadas)
        assertEquals("ADELANTADO", local.estadoPago)
        assertEquals("987654321", local.telefono)
        assertEquals("Academia Norte", local.direccion)
        assertEquals("Golpes muy sólidos", local.notasEntrenador)
        assertEquals(18, local.edad)

        assertNull(local.profesorInstructor)
    }
}
