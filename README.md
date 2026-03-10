# MindMate AI 🧠📚

**MindMate AI** is an intelligent, AI-powered study companion application designed to help students learn faster, comprehend deeply, and test their knowledge. Built entirely using modern Android development practices (**Jetpack Compose** + **MVVM**) and powered by **Google's Gemini 2.5 Flash API**, this application acts as your 24/7 personal tutor.

## ✨ Key Features

MindMate AI processes your academic PDFs or raw code snippets and instantly generates tailored learning materials:

*   **📝 Summarize**: Upload any study notes (PDF) and get a clean, concise, academic summary tailored for engineering students.
*   **🎯 Quick Quiz (Interactive)**: Automatically parses your PDF and generates an interactive Multiple Choice Quiz. Answers light up green/red in real-time to test your retention instantly!
*   **❓ Gen Questions**: Extracts the defining concepts of your document and generates 10-15 highly probable exam questions so you can prep effectively.
*   **🌍 Kannada Mode**: Language shouldn't be a barrier. MindMate translates and explains complex concepts fluently in Kannada.
*   **💡 Real World**: Struggling with abstract theories? This feature explains academic concepts using practical, everyday analogies.
*   **💻 Codium (Code Explainer)**: Paste a confusing block of code, and MindMate will break it down step-by-step for a beginner to understand.
*   **🤖 Smart Chatbot**: Have follow-up questions? Tap the floating robot icon to enter a dedicated chat session with MindMate AI to discuss, debate, or clarify your doubts.

## 🛠️ Tech Stack 

*   **Language**: Kotlin (2.0.21)
*   **UI Toolkit**: Jetpack Compose (Material Design 3)
*   **Architecture**: MVVM (Model-View-ViewModel) with `StateFlow`
*   **AI Engine**: Google Generative AI SDK (`gemini-2.5-flash`)
*   **Document Parsing**: `itextg` (Fast, native PDF text extraction)
*   **Navigation**: Jetpack Navigation Compose
*   **Coroutines & Dispatchers**: For asynchronous, non-blocking API calls and heavy PDF parsing.

## 🚀 Installation & Setup

To run MindMate AI on your local machine, you will need to supply your own Google Gemini API Key.

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Vaishnav2005/MindMate-Ai.git
   ```

2. **Open the project** in **Android Studio**.

3. **Add your Gemini API Key**:
   * Navigate to the root directory of the project.
   * Open the `local.properties` file (create it if it doesn't exist).
   * Add the following line:
     ```properties
     GEMINI_API_KEY=Put_Your_API_Key_Here
     ```
   * *Note: `local.properties` is included in the `.gitignore` to ensure your API keys are never accidentally pushed or exposed publically.*

4. **Sync Gradle & Run**:
   * Let Android Studio sync all Gradle dependencies.
   * Click the **Run (▶)** button to launch the app on your emulator or connected physical Android device.

## 📱 App Architecture

The app is cleanly separated according to recommended Android architecture guidelines:
*   **UI Layer**: Contains stateless composable screens (`HomeScreen`, `ResultScreen`, `ChatbotScreen`) that react predictably to state changes.
*   **ViewModel Layer**: `GeminiViewModel` securely holds API instances, manages long-running conversation history, handles loading states (`CircularProgressIndicator`), and exposes UI-consumable `StateFlows`.
*   **Utility Layer**: Standalone helper objects like `PdfHelper` run on `Dispatchers.IO` to efficiently extract text strings from heavy local `Uri` PDF files without freezing the app UI.

---
*Built with ❤️ for students, by students.*