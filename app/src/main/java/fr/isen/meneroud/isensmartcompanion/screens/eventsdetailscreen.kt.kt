package fr.isen.meneroud.isensmartcompanion.screens


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fr.isen.meneroud.isensmartcompanion.models.Event
import fr.isen.meneroud.isensmartcompanion.notifications.EventNotificationWorker
import java.util.concurrent.TimeUnit

@Composable
fun EventDetailScreen(event: Event?, navController: NavController? = null) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    var isNotified by remember { mutableStateOf(sharedPreferences.getBoolean(event?.id ?: "", false)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        event?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = it.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it.description, fontSize = 16.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "📅 Date : ${it.date}", fontSize = 14.sp)
                    Text(text = "📍 Lieu : ${it.location}", fontSize = 14.sp)
                    Text(text = "📌 Catégorie : ${it.category}", fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Icône de notification
                    IconButton(
                        onClick = {
                            isNotified = !isNotified
                            sharedPreferences.edit().putBoolean(it.id, isNotified).apply()

                            if (isNotified) {
                                scheduleNotification(context, it)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isNotified) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Toggle Notification",
                            tint = if (isNotified) Color.Red else Color.Gray
                        )
                    }
                }
            }
        } ?: Text(text = "Événement introuvable", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.weight(1f))

        // Bouton Retour
        navController?.let {
            Button(
                onClick = { it.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Back", fontSize = 16.sp)
            }
        }
    }
}

fun scheduleNotification(context: Context, event: Event) {
    val workManager = WorkManager.getInstance(context)

    val notificationData = Data.Builder()
        .putString("title", "Rappel d'événement")
        .putString("message", "L'événement '${event.title}' arrive bientôt !")
        .build()

    val workRequest = OneTimeWorkRequestBuilder<EventNotificationWorker>()
        .setInitialDelay(5, TimeUnit.SECONDS) // Notification après 10 secondes
        .setInputData(notificationData)
        .build()

    workManager.enqueue(workRequest)
}