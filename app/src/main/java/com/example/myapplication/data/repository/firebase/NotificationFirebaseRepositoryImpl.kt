package com.example.myapplication.data.repository.firebase

import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.TipoNotificacion
import com.example.myapplication.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
class NotificationFirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private val collection = firestore.collection("notifications")
    private val userFlows = mutableMapOf<String, MutableStateFlow<List<Notification>>>()

    override fun getForUser(userId: String): StateFlow<List<Notification>> {
        return userFlows.getOrPut(userId) {
            val flow = MutableStateFlow<List<Notification>>(emptyList())
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            callbackFlow {
                val listener = collection
                    .whereEqualTo("destinatarioId", userId)
                    .orderBy("creadoEn", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, _ ->
                        snapshot?.let {
                            trySend(it.documents.mapNotNull { doc ->
                                try {
                                    Notification(
                                        id = doc.id,
                                        tipo = TipoNotificacion.valueOf(
                                            doc.getString("tipo") ?: TipoNotificacion.REPORTE_CREADO.name
                                        ),
                                        titulo = doc.getString("titulo") ?: "",
                                        mensaje = doc.getString("mensaje") ?: "",
                                        reporteId = doc.getString("reporteId") ?: "",
                                        destinatarioId = doc.getString("destinatarioId") ?: "",
                                        creadoEn = doc.getLong("creadoEn") ?: 0L,
                                        leido = doc.getBoolean("leido") ?: false,
                                        creadoPorId = doc.getString("creadoPorId") ?: ""
                                    )
                                } catch (e: Exception) { null }
                            })
                        }
                    }
                awaitClose { listener.remove() }
            }.onEach { flow.value = it }.launchIn(scope)
            flow
        }.asStateFlow()
    }

    override suspend fun markAsRead(notifId: String) {
        collection.document(notifId).update("leido", true).await()
    }

    override suspend fun markAllAsRead(userId: String) {
        val batch = firestore.batch()
        collection
            .whereEqualTo("destinatarioId", userId)
            .whereEqualTo("leido", false)
            .get().await()
            .documents
            .forEach { batch.update(it.reference, "leido", true) }
        batch.commit().await()
    }

    override suspend fun create(notification: Notification) {
        val data = mapOf(
            "tipo" to notification.tipo.name,
            "titulo" to notification.titulo,
            "mensaje" to notification.mensaje,
            "reporteId" to notification.reporteId,
            "destinatarioId" to notification.destinatarioId,
            "creadoEn" to notification.creadoEn,
            "leido" to false,
            "creadoPorId" to notification.creadoPorId
        )
        collection.add(data).await()
    }
}
