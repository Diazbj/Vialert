package com.example.myapplication.data.service

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUploadService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Obtén estos valores en cloudinary.com → Dashboard
    private val cloudName = "dupm8asvi"
    private val uploadPreset = "vialert_preset"

    private val client = OkHttpClient()

    suspend fun uploadImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("No se pudo abrir la imagen")

        val bytes = inputStream.readBytes()
        inputStream.close()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "image.jpg",
                bytes.toRequestBody("image/*".toMediaType())
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Respuesta vacía de Cloudinary")

        if (!response.isSuccessful) throw Exception("Error al subir imagen: ${response.code}")

        val urlRegex = "\"secure_url\":\"([^\"]+)\"".toRegex()
        urlRegex.find(body)?.groupValues?.get(1)?.replace("\\/", "/")
            ?: throw Exception("No se pudo obtener la URL de Cloudinary")
    }

    suspend fun uploadReportImage(imageUri: Uri, userId: String): String = uploadImage(imageUri)

    suspend fun uploadProfileImage(imageUri: Uri, userId: String): String = uploadImage(imageUri)
}
