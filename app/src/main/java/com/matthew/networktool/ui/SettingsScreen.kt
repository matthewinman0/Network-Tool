import androidx.compose.runtime.*
import androidx.compose.material3.*

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
    }
}
