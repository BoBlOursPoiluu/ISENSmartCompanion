package fr.isen.meneroud.isensmartcompanion.data

import fr.isen.meneroud.isensmartcompanion.RetrofitInstance
import fr.isen.meneroud.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface EventApi {
    @GET("events") // Remplace cette URL par ton endpoint API correct
    fun getEvents(): Call<List<Event>>
}

object EventRepository {
    fun getEvents(): List<Event> {
        val events = mutableListOf<Event>()

        val call = RetrofitInstance.api.getEvents()
        val response = call.execute() // ⚠️ Cette opération doit être appelée en coroutine normalement

        if (response.isSuccessful) {
            response.body()?.let {
                events.addAll(it)
            }
        }

        return events
    }
}
