package fr.isen.meneroud.isensmartcompanion.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.meneroud.isensmartcompanion.database.ChatDatabase
import fr.isen.meneroud.isensmartcompanion.database.ChatMessage
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val database = ChatDatabase.getDatabase(context)
    val chatDao = database.chatMessageDao()
    val coroutineScope = rememberCoroutineScope()

    // ✅ Récupération des messages de l'historique depuis la base de données
    val messages by chatDao.getAllMessages().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Historique des Conversations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Utilisation de Box pour garantir l'affichage correct
        Box(
            modifier = Modifier
                .weight(1f) // ✅ Permet à la liste de ne pas cacher le bouton
                .fillMaxSize()
        ) {
            if (messages.isEmpty()) {
                Text(
                    text = "Aucun message dans l'historique.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center) // ✅ Alignement au centre
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { message ->
                        HistoryMessageCard(message, onDelete = {
                            coroutineScope.launch {
                                chatDao.deleteMessage(message) // ✅ Supprime un message spécifique
                            }
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton toujours visible en bas
        Button(
            onClick = {
                coroutineScope.launch {
                    chatDao.deleteAllMessages() // ✅ Supprime tout l'historique
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth() // ✅ Corrige l'erreur
                .padding(vertical = 8.dp) // ✅ Ajoute de l'espace
        ) {
            Text(text = "Effacer l'historique", color = Color.White)
        }
    }
}


@Composable
fun HistoryMessageCard(message: ChatMessage, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "👤 ${message.userMessage}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "🤖 ${message.aiResponse}",
                fontSize = 16.sp,
                color = Color.Blue
            )
            Text(
                text = "📅 ${message.timestamp}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer", tint = Color.Red)
                }
            }
        }
    }
}
