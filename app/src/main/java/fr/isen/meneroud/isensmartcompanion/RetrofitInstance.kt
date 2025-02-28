package fr.isen.meneroud.isensmartcompanion

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://isen-smart-companion-default-rtdb.europe-west1.firebasedatabase.app/"

    val api: EventService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // ✅ Convertit JSON en objets Kotlin
            .build()
            .create(EventService::class.java)
    }
}