package fr.isen.meneroud.isensmartcompanion.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import fr.isen.meneroud.isensmartcompanion.GeminiAI
import fr.isen.meneroud.isensmartcompanion.database.ChatDatabase
import fr.isen.meneroud.isensmartcompanion.database.ChatMessage
import kotlinx.coroutines.launch


@Composable
fun MainScreen(navController: NavController, apiKey: String) {
    AssistantUI(apiKey)
}



@Composable
fun AssistantUI(apiKey: String) {
    var question by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiAI = remember { GeminiAI(apiKey) }

    // ✅ Liste des messages affichés (qui peut être nettoyée)
    val displayedMessages = remember { mutableStateListOf<ChatMessage>() }

    // ✅ Accès à la base de données et SharedPreferences
    val database = ChatDatabase.getDatabase(context)
    val chatDao = database.chatMessageDao()
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    // ✅ Vérifie si l'affichage a été nettoyé la dernière fois
    val isCleared = remember { mutableStateOf(sharedPreferences.getBoolean("isCleared", false)) }

    // ✅ Chargement des messages au lancement, sauf si l'affichage a été nettoyé
    LaunchedEffect(Unit) {
        displayedMessages.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Fixe l'entête en haut
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ISEN",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            Text(
                "Smart Companion",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ✅ Affichage des messages si non nettoyé
        if (!isCleared.value) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(displayedMessages) { message ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "👤 ${message.userMessage}",
                            fontSize = 16.sp,
                            color = Color.Black,
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
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // ✅ Bouton plus discret pour vider l'affichage
        OutlinedButton(
            onClick = {
                displayedMessages.clear()
                sharedPreferences.edit().putBoolean("isCleared", true).apply()
                isCleared.value = true
            },
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(120.dp, 32.dp)
        ) {
            Text("🧹 Nettoyer", fontSize = 12.sp, color = Color.Gray)
        }

        // ✅ Barre de question avec bouton d'envoi intégré
        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Posez votre question...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            trailingIcon = { // ✅ Ajout du bouton d'envoi dans le champ
                IconButton(
                    onClick = {
                        if (question.isNotBlank()) {
                            val userMessage = question
                            question = ""

                            coroutineScope.launch {
                                val response = geminiAI.analyzeText(userMessage)
                                val aiResponse = response ?: "No response available"

                                val chatMessage = ChatMessage(
                                    userMessage = userMessage,
                                    aiResponse = aiResponse,
                                    timestamp = System.currentTimeMillis().toString()
                                )

                                chatDao.insertMessage(chatMessage)
                                displayedMessages.add(chatMessage)
                                sharedPreferences.edit().putBoolean("isCleared", false).apply()
                                isCleared.value = false
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Envoyer", tint = Color.Red)
                }
            }
        )
    }
}
