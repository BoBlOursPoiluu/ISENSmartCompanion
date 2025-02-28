package fr.isen.meneroud.isensmartcompanion

import fr.isen.meneroud.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface EventService {
    @GET("events.json") // ✅ Spécifie la requête GET pour récupérer les événements
    fun getEvents(): Call<List<Event>>
}