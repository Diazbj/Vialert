package com.example.myapplication.domain.model

data class Report(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location(),
    val status: ReportStatus = ReportStatus.PENDING,
    val type: String = "",
    val photoUrl: String = "",
    val ownerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val important: Int = 0,
    val importantBy: List<String> = emptyList(),
    val rejectionReason: String = ""
)
