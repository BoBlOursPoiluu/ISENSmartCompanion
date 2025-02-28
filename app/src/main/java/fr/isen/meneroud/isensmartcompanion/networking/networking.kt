package fr.isen.meneroud.isensmartcompanion.networking

import fr.isen.meneroud.isensmartcompanion.models.Event
import fr.isen.meneroud.isensmartcompanion.RetrofitInstance


fun fetchEvents(onResult: (List<Event>) -> Unit) {
    val call = RetrofitInstance.api.getEvents()

    call.enqueue(object : retrofit2.Callback<List<Event>> {
        override fun onResponse(
            call: retrofit2.Call<List<Event>>,
            response: retrofit2.Response<List<Event>>
        ) {
            if (response.isSuccessful) {
                val events = response.body()
                println("✅ Réponse API reçue : $events") // ✅ Vérifie si des données sont reçues
                if (events != null) {
                    onResult(events)
                } else {
                    println("⚠️ Aucune donnée reçue de l'API !")
                }
            } else {
                println("❌ Erreur API: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: retrofit2.Call<List<Event>>, t: Throwable) {
            println("❌ Erreur de récupération des événements: ${t.message}")
        }
    })
}