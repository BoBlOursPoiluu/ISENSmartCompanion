package fr.isen.meneroud.isensmartcompanion.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)


data class Event(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("date") val date: String,
    @SerializedName("location") val location: String,
    @SerializedName("category") val category: String
) : Serializable


