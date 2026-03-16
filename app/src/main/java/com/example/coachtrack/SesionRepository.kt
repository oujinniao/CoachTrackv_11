package com.example.coachtrack


import kotlinx.coroutines.flow.Flow


class SesionRepository(private val dao: SesionDao) {

    fun getSesiones(): Flow<List<SesionEntity>> = dao.getAll()

    fun getSesionesPorAlumno(alumnoId: Long): Flow<List<SesionEntity>> =
        dao.getSesionesPorAlumno(alumnoId)

    suspend fun addSesion(sesion: SesionEntity) = dao.insert(sesion)

    suspend fun deleteSesion(sesion: SesionEntity) = dao.delete(sesion)

    suspend fun deleteAll() = dao.deleteAll()
}

//SesionRepository crea su propia instancia de Room igual que e AlumnoRepository.
//La corrección es inyectarle el SesionDato desde AppContainer en lugar de
// construir la BD internamente