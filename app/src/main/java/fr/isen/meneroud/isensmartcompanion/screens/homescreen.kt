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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch


@Composable
fun MainScreen(navController: NavController, apiKey: String) {
    AssistantUI(apiKey)
}



@Composable
fun AssistantUI(apiKey: String) {
    var question by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiAI = remember { GeminiAI(apiKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 48.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                messages.forEach { msg ->
                    Text(
                        text = msg,
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = if (msg.startsWith("You:")) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = question,
                onValueChange = { question = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (question.isEmpty()) Text("Ask me anything...", color = Color.Gray)
                        innerTextField()
                    }
                }
            )
            IconButton(
                onClick = {
                    if (question.isNotBlank()) {
                        val userMessage = "Vous: $question"
                        messages = messages + userMessage
                        val input = question
                        question = ""

                        coroutineScope.launch {
                            val response = geminiAI.analyzeText(input)
                            messages = messages + "Gemini: $response"
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Red)
            }
        }
    }
}
