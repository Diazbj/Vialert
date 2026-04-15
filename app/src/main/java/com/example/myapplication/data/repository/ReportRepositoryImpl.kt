package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor() : ReportRepository {
    private val items = mutableListOf<Report>()

    init {
        items.addAll(listOf(
            Report(
                id = "101",
                title = "Bache peligroso",
                description = "Bache de gran tamaño en medio de la avenida.",
                location = Location(-25.2822, -57.6351),
                status = ReportStatus.PENDING,
                type = "Infraestructura",
                photoUrl = "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=800&auto=format&fit=crop",
                ownerId = "1"
            ),
            Report(
                id = "102",
                title = "Semáforo averiado",
                description = "El semáforo no cambia a verde.",
                location = Location(-25.2950, -57.5800),
                status = ReportStatus.IN_PROGRESS,
                type = "Tránsito",
                photoUrl = "https://images.unsplash.com/photo-1515511856280-7b23f68d2996?q=80&w=800&auto=format&fit=crop",
                ownerId = "3"
            ),
            Report(
                id = "103",
                title = "Basura acumulada",
                description = "Gran cantidad de desperdicios en la vereda.",
                location = Location(-25.3000, -57.6000),
                status = ReportStatus.RESOLVED,
                type = "Limpieza",
                photoUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?q=80&w=800&auto=format&fit=crop",
                ownerId = "1"
            )
        ))
    }

    override fun getAll(): List<Report> = items.toList()

    override fun getById(id: String): Report? = items.find { it.id == id }

    override fun create(report: Report) {
        items.add(report)
    }

    override fun update(report: Report) {
        val index = items.indexOfFirst { it.id == report.id }
        if (index != -1) {
            items[index] = report
        }
    }

    override fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    override fun getByUserId(userId: String): List<Report> {
        return items.filter { it.ownerId == userId }
    }
}
