package com.example.myapplication.data.repository.firebase

import com.example.myapplication.domain.model.Gender
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Singleton
class UserFirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val collection = firestore.collection("users")

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        // Escuchar cambios en tiempo real en Firestore
        callbackFlow {
            val listener = collection.addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val list = it.documents.mapNotNull { doc ->
                        try {
                            val role = try { UserRole.valueOf(doc.getString("role") ?: "USER") } catch (e: Exception) { UserRole.USER }
                            val gender = doc.getString("gender")?.let { g -> try { Gender.valueOf(g) } catch (e: Exception) { null } }
                            User(
                                id = doc.id,
                                firstName = doc.getString("firstName") ?: "",
                                lastName = doc.getString("lastName") ?: "",
                                email = doc.getString("email") ?: "",
                                userName = doc.getString("userName") ?: "",
                                password = "",
                                gender = gender,
                                birthDate = doc.getString("birthDate") ?: "",
                                phone = doc.getString("phone") ?: "",
                                bio = doc.getString("bio") ?: "",
                                score = (doc.getLong("score") ?: 0L).toInt(),
                                profilePictureUrl = doc.getString("profilePictureUrl") ?: "",
                                role = role
                            )
                        } catch (e: Exception) { null }
                    }
                    trySend(list)
                }
            }
            awaitClose { listener.remove() }
        }.onEach { _users.value = it }.launchIn(scope)
    }

    override fun getAll(): List<User> = _users.value

    override fun getById(id: String): User? = _users.value.find { it.id == id }

    override suspend fun create(user: User) {
        // Crear usuario en Firebase Auth
        val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo crear el usuario")

        // Guardar datos del usuario en Firestore (sin contraseña)
        val data = mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "userName" to user.userName,
            "gender" to (user.gender?.name ?: ""),
            "birthDate" to user.birthDate,
            "phone" to user.phone,
            "bio" to user.bio,
            "score" to user.score,
            "profilePictureUrl" to user.profilePictureUrl,
            "role" to user.role.name
        )
        collection.document(uid).set(data).await()
    }

    override suspend fun update(user: User) {
        val data = mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "userName" to user.userName,
            "gender" to (user.gender?.name ?: ""),
            "birthDate" to user.birthDate,
            "phone" to user.phone,
            "bio" to user.bio,
            "score" to user.score,
            "profilePictureUrl" to user.profilePictureUrl,
            "role" to user.role.name
        )
        collection.document(user.id).update(data).await()
    }

    override fun delete(id: String) {
        collection.document(id).delete()
    }

    override fun findByEmail(email: String): User? = _users.value.find { it.email == email }

    override suspend fun findByEmailAndPassword(email: String, password: String): User? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return null
            // Esperar a que los datos lleguen del listener, o consultar directamente
            val doc = collection.document(uid).get().await()
            val role = try { UserRole.valueOf(doc.getString("role") ?: "USER") } catch (e: Exception) { UserRole.USER }
            val gender = doc.getString("gender")?.let { g -> try { Gender.valueOf(g) } catch (e: Exception) { null } }
            User(
                id = uid,
                firstName = doc.getString("firstName") ?: "",
                lastName = doc.getString("lastName") ?: "",
                email = doc.getString("email") ?: "",
                userName = doc.getString("userName") ?: "",
                password = "",
                gender = gender,
                birthDate = doc.getString("birthDate") ?: "",
                phone = doc.getString("phone") ?: "",
                bio = doc.getString("bio") ?: "",
                score = (doc.getLong("score") ?: 0L).toInt(),
                profilePictureUrl = doc.getString("profilePictureUrl") ?: "",
                role = role
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}
