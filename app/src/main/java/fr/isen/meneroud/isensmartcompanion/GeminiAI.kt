package fr.isen.meneroud.isensmartcompanion

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart

class GeminiAI(private val apiKey: String) {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun analyzeText(userInput: String): String {
        return try {
            val response = model.generateContent(userInput)
            response.text ?: "Aucune réponse de l'IA."
        } catch (e: Exception) {
            "Erreur lors de l'analyse: ${e.message}"
        }
    }
}
