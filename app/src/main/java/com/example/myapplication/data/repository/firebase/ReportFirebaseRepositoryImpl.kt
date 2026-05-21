package com.example.myapplication.data.repository.firebase

import com.example.myapplication.domain.model.Location
import com.example.myapplication.domain.model.Report
import com.example.myapplication.domain.model.ReportStatus
import com.example.myapplication.domain.repository.ReportRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportFirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("reports")

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    override val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    init {
        callbackFlow {
            val listener = collection
                .whereNotEqualTo("status", ReportStatus.DELETED.name)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val list = it.documents.mapNotNull { doc ->
                            try {
                                val lat = doc.getDouble("latitude") ?: 0.0
                                val lng = doc.getDouble("longitude") ?: 0.0
                                val status = try { ReportStatus.valueOf(doc.getString("status") ?: "PENDING") } catch (e: Exception) { ReportStatus.PENDING }
                                Report(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    description = doc.getString("description") ?: "",
                                    location = Location(lat, lng),
                                    status = status,
                                    type = doc.getString("type") ?: "",
                                    photoUrl = doc.getString("photoUrl") ?: "",
                                    ownerId = doc.getString("ownerId") ?: "",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                    important = (doc.getLong("important") ?: 0L).toInt(),
                                    importantBy = (doc.get("importantBy") as? List<*>)
                                        ?.filterIsInstance<String>() ?: emptyList()
                                )
                            } catch (e: Exception) { null }
                        }
                        trySend(list)
                    }
                }
            awaitClose { listener.remove() }
        }.onEach { _reports.value = it }.launchIn(scope)
    }

    override fun getAll(): List<Report> = _reports.value

    override fun getById(id: String): Report? = _reports.value.find { it.id == id }

    override suspend fun create(report: Report) {
        val data = mapOf(
            "title" to report.title,
            "description" to report.description,
            "latitude" to report.location.latitude,
            "longitude" to report.location.longitude,
            "status" to report.status.name,
            "type" to report.type,
            "photoUrl" to report.photoUrl,
            "ownerId" to report.ownerId,
            "createdAt" to System.currentTimeMillis(),
            "important" to 0
        )
        collection.add(data).await()
    }

    override suspend fun update(report: Report) {
        val data = mapOf(
            "title" to report.title,
            "description" to report.description,
            "latitude" to report.location.latitude,
            "longitude" to report.location.longitude,
            "status" to report.status.name,
            "type" to report.type,
            "photoUrl" to report.photoUrl,
            "ownerId" to report.ownerId,
            "createdAt" to report.createdAt,
            "important" to report.important
        )
        collection.document(report.id).set(data).await()
    }

    override suspend fun delete(id: String) {
        collection.document(id).update("status", ReportStatus.DELETED.name).await()
    }

    override fun getByUserId(userId: String): List<Report> = _reports.value.filter { it.ownerId == userId }

    override suspend fun incrementarImportancia(id: String, userId: String) {
        val docRef = collection.document(id)
        firestore.runTransaction { transaction ->
            val snap = transaction.get(docRef)
            val voters = (snap.get("importantBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            if (userId !in voters) {
                val current = snap.getLong("important")?.toInt() ?: 0
                transaction.update(docRef, mapOf(
                    "important" to current + 1,
                    "importantBy" to voters + userId
                ))
            }
        }.await()
    }
}
