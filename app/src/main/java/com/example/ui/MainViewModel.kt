package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookEntity
import com.example.data.LoanEntity
import com.example.data.PerpusRepository
import com.example.data.SyncLogEntity
import com.example.service.AiGuruEngine
import com.example.service.GeneratedQuiz
import com.example.service.NasSyncEngine
import com.example.service.SimplifiedParagraph
import com.example.service.TtsEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "GURU_AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = PerpusRepository(db.bookDao(), db.loanDao())
    val nasEngine = NasSyncEngine(repository)
    val ttsEngine = TtsEngine(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val books: StateFlow<List<BookEntity>> = combine(
        repository.allBooks,
        _searchQuery,
        _selectedCategory
    ) { bookList, query, category ->
        bookList.filter { book ->
            val matchesQuery = query.isEmpty() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)
            val matchesCategory = category == "Semua" || book.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loans: StateFlow<List<LoanEntity>> = repository.allLoans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val syncLogs: StateFlow<List<SyncLogEntity>> = repository.syncLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reader Mode State
    private val _selectedBook = MutableStateFlow<BookEntity?>(null)
    val selectedBook: StateFlow<BookEntity?> = _selectedBook.asStateFlow()

    private val _simplifiedParagraphs = MutableStateFlow<List<SimplifiedParagraph>>(emptyList())
    val simplifiedParagraphs: StateFlow<List<SimplifiedParagraph>> = _simplifiedParagraphs.asStateFlow()

    private val _highlightedSentenceIndex = MutableStateFlow(0)
    val highlightedSentenceIndex: StateFlow<Int> = _highlightedSentenceIndex.asStateFlow()

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    private val _ttsRate = MutableStateFlow(1.0f)
    val ttsRate: StateFlow<Float> = _ttsRate.asStateFlow()

    private val _ttsGender = MutableStateFlow("FEMALE")
    val ttsGender: StateFlow<String> = _ttsGender.asStateFlow()

    // GuruAI Chat & Quiz
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("GURU_AI", "Halo! Saya GuruAI. Ada yang mau kamu tanyakan tentang isi buku perpustakaan atau butuh ringkasan materi?")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _generatedQuiz = MutableStateFlow<GeneratedQuiz?>(null)
    val generatedQuiz: StateFlow<GeneratedQuiz?> = _generatedQuiz.asStateFlow()

    init {
        viewModelScope.launch {
            repository.prepopulateInitialBooksIfEmpty()
        }

        ttsEngine.setHighlightListener { index ->
            _highlightedSentenceIndex.value = index
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openBookForReading(book: BookEntity) {
        _selectedBook.value = book
        // Pre-generate AI Simplifier text for chapter
        val defaultContent = "Hukum Newton pertama menyatakan bahwa benda diam akan tetap diam kecuali ada gaya luar. " +
                "Hukum Newton kedua menjelaskan percepatan berbanding lurus dengan gaya net. " +
                "Hukum Newton ketiga menyatakan untuk setiap aksi selalu ada reaksi yang sama besar dan berlawanan arah. " +
                "Koneksi energi kinetik dan potensial menjaga sistem mekanis berada dalam keseimbangan konstan."

        val simplified = AiGuruEngine.simplifyTextForSmp(defaultContent, book.title)
        _simplifiedParagraphs.value = simplified

        if (simplified.isNotEmpty()) {
            ttsEngine.loadSentences(simplified[0].simplifiedText)
        }
    }

    fun togglePlayTts() {
        if (_isTtsPlaying.value) {
            ttsEngine.pause()
            _isTtsPlaying.value = false
        } else {
            ttsEngine.play()
            _isTtsPlaying.value = true
        }
    }

    fun repeatParagraphTts() {
        ttsEngine.repeatParagraph()
        _isTtsPlaying.value = true
    }

    fun setTtsRate(rate: Float) {
        _ttsRate.value = rate
        ttsEngine.setRate(rate)
    }

    fun setTtsGender(gender: String) {
        _ttsGender.value = gender
        ttsEngine.setVoiceType(gender)
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage("USER", text))

        val answer = when {
            text.contains("fisika", ignoreCase = true) ->
                "Jadi gini, fisika di buku kita membahas gaya, energi, dan gerakan. Paham ya, intinya gaya bikin benda bergerak!"
            text.contains("sejarah", ignoreCase = true) ->
                "Nusantara punya sejarah kaya dari Kerajaan Kutai sampai Proklamasi 1945. Contohnya diplomasi bangsa kita sangat gigih!"
            text.contains("kuis", ignoreCase = true) ->
                "Kamu bisa buka tab 'GuruAI' lalu klik 'Generate Kuis' buat latihan 10 Pilihan Ganda + 5 Esai!"
            else ->
                "Penjelasan GuruAI: ${text.take(40)}... Pembahasan di buku perpustakaan menekankan penerapan praktis dengan bahasa yang mudah dipahami anak SMP!"
        }

        current.add(ChatMessage("GURU_AI", answer))
        _chatMessages.value = current
    }

    fun generateQuizForBook(book: BookEntity) {
        val quiz = AiGuruEngine.generateQuizFromPdf(book.description + " " + book.title, book.title)
        _generatedQuiz.value = quiz
    }

    fun borrowBook(bookId: Int, name: String) {
        viewModelScope.launch {
            repository.borrowBook(bookId, name, "2026-07-28", "2026-08-11")
        }
    }

    fun returnBook(bookId: Int) {
        viewModelScope.launch {
            repository.returnBook(bookId, "2026-07-28")
        }
    }

    fun addNewBook(title: String, author: String, category: String, desc: String) {
        viewModelScope.launch {
            repository.insertBook(
                BookEntity(
                    title = title,
                    author = author,
                    category = category,
                    description = desc
                )
            )
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            repository.deleteBook(id)
        }
    }

    fun triggerNasSync() {
        viewModelScope.launch {
            nasEngine.performSync()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
    }
}
