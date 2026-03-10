package com.vaishnav.mindmateai.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.vaishnav.mindmateai.R
import com.vaishnav.mindmateai.util.PdfHelper
import com.vaishnav.mindmateai.viewmodel.GeminiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: GeminiViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MindMate AI", fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5)) },
                actions = {
                    TextButton(onClick = { /* Navigate About Us */ }) { Text("About Us", color = Color(0xFF4F46E5)) }
                    TextButton(onClick = { /* Navigate Contact Us */ }) { Text("Contact Us", color = Color(0xFF4F46E5)) }
                    IconButton(onClick = { /* Navigate Profile */ }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color(0xFF4F46E5))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("chatbot") },
                containerColor = Color(0xFF22C55E),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chatbot),
                    contentDescription = "Chat with AI",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "What would you like to study today?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val features = listOf(
                FeatureItem("Summarize", "Quick AI summary of notes", Color(0xFF90D5FF), FeatureMode.PDF, R.drawable.ic_summarize),
                FeatureItem("Quick Quiz", "Generate 10 MCQ quiz instantly", Color(0xFFFFF44F), FeatureMode.PDF, R.drawable.ic_quiz),
                FeatureItem("Gen Questions", "10-15 important exam questions", Color(0xFFFF2C2C), FeatureMode.PDF, R.drawable.ic_question),
                FeatureItem("Kannada Mode", "Concepts explained in Kannada", Color(0xFFFFDBBB), FeatureMode.PDF, R.drawable.ic_language),
                FeatureItem("Real World", "Understand using analogies", Color(0xFFFFB6C1), FeatureMode.PDF, R.drawable.ic_real_world),
                FeatureItem("Codium", "Explain code step-by-step", Color(0xFF88E788), FeatureMode.TEXT, R.drawable.ic_code)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(feature) { mode, uri, text ->
                        if (mode == FeatureMode.TEXT) {
                            if (text.isNotBlank()) {
                                viewModel.explainCode(text)
                                  navController.navigate("result/${android.net.Uri.encode(feature.title)}")
                              }
                        } else if (uri != null) {
                            coroutineScope.launch {
                                // Extract PDF text in IO Thread
                                val parsedText = withContext(Dispatchers.IO) {
                                    PdfHelper.extractTextFromUri(context, uri)
                                }
                                
                                // Call respective ViewModel logic
                                when (feature.title) {
                                    "Summarize" -> viewModel.summarizeContent(parsedText)
                                    "Quick Quiz" -> viewModel.generateMCQ(parsedText)
                                    "Gen Questions" -> viewModel.generateQuestions(parsedText)
                                    "Kannada Mode" -> viewModel.explainKannada(parsedText)
                                    "Real World" -> viewModel.realWorldAnalogy(parsedText)
                                }
                                
                                navController.navigate("result/${android.net.Uri.encode(feature.title)}")
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class FeatureMode { PDF, TEXT }

data class FeatureItem(
    val title: String,
    val description: String,
    val color: Color,
    val mode: FeatureMode,
    val iconRes: Int
)

@Composable
fun FeatureCard(feature: FeatureItem, onClick: (FeatureMode, Uri?, String) -> Unit) {
    var textInput by remember { mutableStateOf("") }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            onClick(FeatureMode.PDF, uri, "")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Adjusted height to fit UI comfortably
            .clickable(enabled = feature.mode == FeatureMode.PDF) {
                pdfLauncher.launch("application/pdf")
            },
        colors = CardDefaults.cardColors(containerColor = feature.color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = feature.iconRes),
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = if(feature.color == Color(0xFFFF2C2C)) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(feature.color == Color(0xFFFF2C2C)) Color.White else Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(feature.description, fontSize = 12.sp, textAlign = TextAlign.Center, color = if(feature.color == Color(0xFFFF2C2C)) Color.White else Color.Black)

            Spacer(modifier = Modifier.weight(1f))
            if (feature.mode == FeatureMode.TEXT) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Code doubt?", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { 
                        if (textInput.isNotBlank()) {
                            onClick(feature.mode, null, textInput) 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("Explain Code", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { pdfLauncher.launch("application/pdf") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Upload PDF", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
