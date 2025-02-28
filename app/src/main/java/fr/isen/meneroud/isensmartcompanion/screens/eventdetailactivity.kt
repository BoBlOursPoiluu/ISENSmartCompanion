package fr.isen.meneroud.isensmartcompanion.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.meneroud.isensmartcompanion.models.Event
import fr.isen.meneroud.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Récupérer l'événement depuis l'Intent
        val event: Event? = intent.getParcelableExtra("event")

        setContent {
            ISENSmartCompanionTheme {
                EventDetailScreen(event) // 🔹 Affiche les détails de l'événement
            }
        }
    }
}