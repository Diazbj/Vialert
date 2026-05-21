package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor() : ReportRepository {
    private val _reports = MutableStateFlow<List<Report>>(fetchInitialReports())
    override val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    override fun getAll(): List<Report> = _reports.value

    override fun getById(id: String): Report? = _reports.value.find { it.id == id }

    override suspend fun create(report: Report) {
        _reports.value = _reports.value + report.copy(createdAt = System.currentTimeMillis(), important = 0)
    }

    override suspend fun update(report: Report) {
        _reports.value = _reports.value.map { if (it.id == report.id) report else it }
    }

    override suspend fun delete(id: String) {
        _reports.value = _reports.value.filter { it.id != id }
    }

    override fun getByUserId(userId: String): List<Report> = _reports.value.filter { it.ownerId == userId }

    override suspend fun incrementarImportancia(id: String, userId: String) {
        _reports.value = _reports.value.map {
            if (it.id == id && userId !in it.importantBy)
                it.copy(important = it.important + 1, importantBy = it.importantBy + userId)
            else it
        }
    }

    private fun fetchInitialReports(): List<Report> {
        val now = System.currentTimeMillis()
        return listOf(
            Report(id = "101", title = "Bache peligroso", description = "Bache de gran tamaño en medio de la avenida.", location = Location(-25.2822, -57.6351), status = ReportStatus.PENDING, type = "Infraestructura", photoUrl = "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=800", ownerId = "1", createdAt = now - 720000),
            Report(id = "102", title = "Semáforo averiado", description = "El semáforo no cambia a verde.", location = Location(-25.2950, -57.5800), status = ReportStatus.IN_PROGRESS, type = "Seguridad Vial", photoUrl = "https://images.unsplash.com/photo-1515511856280-7b23f68d2996?q=80&w=800", ownerId = "3", createdAt = now - 3600000),
            Report(id = "103", title = "Falta de iluminación", description = "Calle muy oscura por falta de focos.", location = Location(-25.3000, -57.6000), status = ReportStatus.RESOLVED, type = "Alumbrado", photoUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?q=80&w=800", ownerId = "1", createdAt = now - 10800000)
        )
    }
}
