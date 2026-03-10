package com.vaishnav.mindmateai.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vaishnav.mindmateai.viewmodel.GeminiViewModel
import org.json.JSONArray

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

fun parseQuizJson(jsonString: String): List<QuizQuestion> {
    val questions = mutableListOf<QuizQuestion>()
    try {
        var cleanJson = jsonString.trim()
        if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substringAfter("```json").substringAfter("```").substringBeforeLast("```").trim()
        }
        val array = JSONArray(cleanJson)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val qText = obj.getString("question")
            val optArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optArray.length()) {
                options.add(optArray.getString(j))
            }
            val ansIndex = obj.getInt("correctAnswerIndex")
            questions.add(QuizQuestion(qText, options, ansIndex))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return questions
}

@Composable
fun InteractiveQuiz(jsonResponse: String) {
    val questions = remember(jsonResponse) { parseQuizJson(jsonResponse) }
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }

    if (questions.isEmpty()) {
       Text("Generating interactive format... or an error parsing. Raw reply:\n\n$jsonResponse", color = Color.Black)
       return
    }

    if (isQuizFinished) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz Completed!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                "Your Score: $score / ${questions.size}",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        val currentQuestion = questions[currentQuestionIndex]

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = currentQuestion.question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == currentQuestion.correctIndex
                
                val backgroundColor = when {
                    selectedOptionIndex == null -> Color.White
                    isCorrect && selectedOptionIndex != null -> Color(0xFF4CAF50) // Show green if it is the correct answer anytime an option is picked
                    isSelected && !isCorrect -> Color(0xFFF44336) // Show red if it was selected and wrong
                    else -> Color.White
                }
                
                val textColor = if (backgroundColor == Color.White) Color.Black else Color.White

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable(enabled = selectedOptionIndex == null) {
                            selectedOptionIndex = index
                            if (index == currentQuestion.correctIndex) {
                                score++
                            }
                        }
                        .padding(16.dp)
                ) {
                    Text(text = option, color = textColor, fontSize = 16.sp)
                }
            }

            if (selectedOptionIndex != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            selectedOptionIndex = null
                        } else {
                            isQuizFinished = true
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text(if (currentQuestionIndex < questions.size - 1) "Next" else "Finish", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, viewModel: GeminiViewModel, title: String) {
    val response by viewModel.responseState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val cardColor = when (title) {
        "Summarize" -> Color(0xFF90D5FF)
        "Quick Quiz" -> Color(0xFFFFF44F)
        "Gen Questions" -> Color(0xFFFF2C2C)
        "Kannada Mode" -> Color(0xFFFFDBBB)
        "Real World" -> Color(0xFFFFB6C1)
        "Codium" -> Color(0xFF88E788)
        else -> Color(0xFF4F46E5)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = if (title == "Quick Quiz") Color.Black else Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            if (title == "Quick Quiz") "Generating interactive quiz..." else "Generating content...", 
                            color = if (title == "Quick Quiz") Color.Black else Color.White
                        )
                    } else if (response.isNotEmpty()) {
                        if (title == "Quick Quiz") {
                            InteractiveQuiz(response)
                        } else {
                            val textColor = if (title == "Quick Quiz") Color.Black else Color.White
                            Text(
                                text = response,
                                color = textColor,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        }
                    } else {
                        Text(
                            text = "No response generated.",
                            color = if (title == "Quick Quiz") Color.Black else Color.White,
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}