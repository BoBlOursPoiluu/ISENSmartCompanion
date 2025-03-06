package fr.isen.meneroud.isensmartcompanion.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.meneroud.isensmartcompanion.models.Event
import fr.isen.meneroud.isensmartcompanion.networking.fetchEvents


fun getFakeCourses(): List<Event> {
    return listOf(
        Event("101", "Mathématiques", "Cours de mathématiques", "2025-03-05", "Salle A1", "Cours"),
        Event("102", "Physique", "Cours de physique", "2025-03-06", "Salle B2", "Cours"),
        Event("103", "Informatique", "Cours de programmation", "2025-03-07", "Salle C3", "Cours"),
        Event("104", "Électronique", "Cours sur les circuits", "2025-03-08", "Salle D4", "Cours"),
        Event("105", "Cryptographie", "Cours sur la sécurité des données", "2025-03-09", "Salle E5", "Cours")
    )
}

fun groupEventsByDate(events: List<Event>): Map<String, List<Event>> {
    return events.groupBy { it.date }
}


@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    val savedEvents = remember { mutableStateListOf<Event>() }
    val fakeCourses = remember { getFakeCourses() }

    // Charger les événements notifiés
    LaunchedEffect(Unit) {
        fetchEvents { events ->
            val notifiedEvents = events.filter { sharedPreferences.getBoolean(it.id, false) }
            savedEvents.clear()
            savedEvents.addAll(notifiedEvents)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📅 Mon Agenda", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        if (savedEvents.isEmpty()) {
            Text("Aucun événement enregistré.", fontSize = 16.sp, color = Color.Gray)
        } else {
            val allEvents = remember { savedEvents + fakeCourses } // ✅ Fusionne événements et cours
            val groupedEvents = remember { groupEventsByDate(allEvents) } // ✅ Regroupe par date

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                groupedEvents.forEach { (date, events) ->
                    item {
                        Text(
                            text = "📆 $date",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("EventDetail/${event.id}") },
                            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        event.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("📍 ${event.location}", fontSize = 14.sp)
                                }

                                // ✅ Affiche le bouton de suppression UNIQUEMENT pour les événements utilisateur
                                if (savedEvents.contains(event)) {
                                    IconButton(
                                        onClick = {
                                            removeEventFromAgenda(context, event.id)
                                            savedEvents.remove(event)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

            // 🔹 Fonction pour supprimer un événement de l'Agenda
            fun removeEventFromAgenda(context: Context, eventId: String) {
                val sharedPreferences =
                    context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().remove(eventId).apply()
            }
