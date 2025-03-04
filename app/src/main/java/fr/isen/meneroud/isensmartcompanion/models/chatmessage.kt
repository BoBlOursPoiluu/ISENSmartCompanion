package fr.isen.meneroud.isensmartcompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userMessage: String,
    val aiResponse: String,
    val timestamp: String
)
