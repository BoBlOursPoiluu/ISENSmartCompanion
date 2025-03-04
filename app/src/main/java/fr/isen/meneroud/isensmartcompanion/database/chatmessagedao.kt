package fr.isen.meneroud.isensmartcompanion.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Delete
    suspend fun deleteMessage(message: ChatMessage) // ✅ Supprime un seul message

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages() // ✅ Supprime tous les messages
}
