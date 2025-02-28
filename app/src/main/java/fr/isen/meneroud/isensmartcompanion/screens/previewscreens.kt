package fr.isen.meneroud.isensmartcompanion.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import fr.isen.meneroud.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

@Preview(showBackground = true)
@Composable
fun AssistantUIPreview() {
    val fakeNavController = rememberNavController() // ✅ Création d'un NavController fictif
    val fakeApiKey = "TEST_API_KEY" // ✅ Clé API factice pour l'aperçu

    ISENSmartCompanionTheme {
        MainScreen(fakeNavController, fakeApiKey) // ✅ apiKey ajouté
    }
}