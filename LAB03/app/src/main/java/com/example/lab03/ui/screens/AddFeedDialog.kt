package com.example.lab03.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddFeedDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAddFeed: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add RSS Feed") },
            text = {
                Column {
                    Text("Enter the RSS feed URL:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        placeholder = { Text("https://news.yam.md/ro/rss") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (url.isNotBlank()) {
                            onAddFeed(url.trim())
                            url = ""
                            onDismiss()
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
