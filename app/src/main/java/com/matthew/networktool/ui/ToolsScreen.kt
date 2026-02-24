import androidx.compose.runtime.*
import androidx.compose.material3.*

@Composable
fun ToolsScreen() {

    val tools = remember {
        mutableStateListOf(
            "Ping Tool",
            "Port Scanner",
            "DNS Lookup",
            "IP Info"
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tools) { tool ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Add to home selection logic */ }
            ) {
                Text(
                    text = tool,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
