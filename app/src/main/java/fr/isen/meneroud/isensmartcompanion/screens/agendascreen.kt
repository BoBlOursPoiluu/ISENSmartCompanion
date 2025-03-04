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

@Composable
fun AgendaScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    val savedEvents = remember { mutableStateListOf<Event>() }

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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(savedEvents) { event ->
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
                            Column(
                                modifier = Modifier.weight(1f) // Prend tout l'espace dispo sauf icône
                            ) {
                                Text(event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("📅 ${event.date}", fontSize = 14.sp)
                                Text("📍 ${event.location}", fontSize = 14.sp)
                            }

                            // 🔹 Icône de suppression bien positionnée à droite
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

// 🔹 Fonction pour supprimer un événement de l'Agenda
fun removeEventFromAgenda(context: Context, eventId: String) {
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove(eventId).apply()
}
