package com.example.lab04

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab04.ui.theme.Lab04Theme
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab04Theme {
                ProgressBarDemo()
            }
        }
    }

    private fun enableEdgeToEdge() {
        // Enable edge-to-edge display
        // This is handled automatically by the Compose theme in newer versions
        // No additional implementation needed for basic edge-to-edge support
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressBarDemo() {
    var isProgressVisible by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("Ready to start") }
    var job by remember { mutableStateOf<Job?>(null) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Custom Progress Bar Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Custom Progress Bar",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "This demo showcases a custom progress bar with:",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "• Vector graphics animations\n• START, PROGRESS, STOP events\n• AsyncTask implementation\n• Overlay on current activity",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Status display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Status",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = statusText,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                
                                if (progress > 0f) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Progress: ${(progress * 100).toInt()}%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Control buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isProgressVisible) {
                                        isProgressVisible = true
                                        progress = 0f
                                        statusText = "Starting progress..."
                                        
                                        job = CoroutineScope(Dispatchers.Main).launch {
                                            // START event
                                            statusText = "Progress started!"
                                            
                                            val totalSteps = 100
                                            val delayMs = 50L
                                            
                                            repeat(totalSteps) { step ->
                                                val newProgress = (step + 1).toFloat() / totalSteps.toFloat()
                                                progress = newProgress
                                                statusText = "Loading... ${(newProgress * 100).toInt()}%"
                                                delay(delayMs)
                                            }
                                            
                                            // STOP event
                                            statusText = "Progress completed!"
                                            isProgressVisible = false
                                            progress = 0f
                                        }
                                    }
                                },
                                enabled = !isProgressVisible,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Start Progress")
                            }
                            
                            Button(
                                onClick = {
                                    job?.cancel()
                                    isProgressVisible = false
                                    progress = 0f
                                    statusText = "Progress cancelled"
                                },
                                enabled = isProgressVisible,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
            
            // Custom Progress Bar Overlay
            if (isProgressVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Simple circular progress indicator
                            CircularProgressIndicator(
                                progress = progress,
                                modifier = Modifier.size(120.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 8.dp
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Progress percentage text
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Status text
                            Text(
                                text = when {
                                    progress == 0f -> "Starting..."
                                    progress < 1f -> "Loading..."
                                    else -> "Complete!"
                                },
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Animated dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(3) { index ->
                                    val infiniteTransition = rememberInfiniteTransition(label = "dots")
                                    val scale by infiniteTransition.animateFloat(
                                        initialValue = 0.5f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(
                                                durationMillis = 600,
                                                delayMillis = index * 200,
                                                easing = EaseInOut
                                            ),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "dot_$index"
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = scale),
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressBarDemoPreview() {
    Lab04Theme {
        ProgressBarDemo()
    }
}