package com.example.data

import kotlinx.coroutines.flow.Flow

class PerpusRepository(
    private val bookDao: BookDao,
    private val loanDao: LoanDao
) {
    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()
    val syncLogs: Flow<List<SyncLogEntity>> = loanDao.getSyncLogs()

    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)

    suspend fun getBookById(id: Int): BookEntity? = bookDao.getBookById(id)

    suspend fun insertBook(book: BookEntity): Long = bookDao.insertBook(book)

    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)

    suspend fun deleteBook(id: Int) = bookDao.deleteBookById(id)

    suspend fun borrowBook(bookId: Int, borrowerName: String, borrowDate: String, returnDueDate: String) {
        val book = bookDao.getBookById(bookId) ?: return
        val updatedBook = book.copy(
            isBorrowed = true,
            borrowerName = borrowerName,
            borrowDate = borrowDate,
            returnDueDate = returnDueDate
        )
        bookDao.updateBook(updatedBook)

        val loan = LoanEntity(
            bookId = bookId,
            bookTitle = book.title,
            borrowerName = borrowerName,
            borrowDate = borrowDate,
            returnDueDate = returnDueDate,
            isReturned = false
        )
        loanDao.insertLoan(loan)
    }

    suspend fun returnBook(bookId: Int, returnDate: String) {
        val book = bookDao.getBookById(bookId) ?: return
        val updatedBook = book.copy(
            isBorrowed = false,
            borrowerName = null,
            borrowDate = null,
            returnDueDate = null
        )
        bookDao.updateBook(updatedBook)
    }

    suspend fun addSyncLog(status: String, details: String) {
        loanDao.insertSyncLog(
            SyncLogEntity(
                status = status,
                details = details
            )
        )
    }

    suspend fun prepopulateInitialBooksIfEmpty() {
        if (bookDao.getBookCount() == 0) {
            val initialList = listOf(
                BookEntity(
                    title = "Fisika Dasar & Terapan SMP",
                    author = "Dr. Bambang Sutrisno",
                    category = "Sains & Teknologi",
                    description = "Membedah Hukum Newton, Energi Kinetik, dan Kelistrikan dengan simulasi sederhana yang mudah dipahami.",
                    pdfFileName = "fisika_dasar_smp.pdf",
                    totalPages = 145
                ),
                BookEntity(
                    title = "Sejarah Nusantara: Dari Kutai hingga Proklamasi",
                    author = "Prof. Retno Wulandari",
                    category = "Sejarah & Budaya",
                    description = "Kisah perjalanan kerajaan-kerajaan besar Nusantara, diplomasi, dan perjuangan kemerdekaan Indonesia.",
                    pdfFileName = "sejarah_nusantara.pdf",
                    totalPages = 210
                ),
                BookEntity(
                    title = "Matematika Logika & Aljabar Praktis",
                    author = "Ir. Heru Kurniawan",
                    category = "Matematika",
                    description = "Panduan pemecahan soal persamaan linear, fungsi kuadrat, dan penalaran logika matematika.",
                    pdfFileName = "matematika_logika.pdf",
                    totalPages = 180
                ),
                BookEntity(
                    title = "Bahasa Indonesia Komunikatif & Sastra",
                    author = "Dra. Nurul Hidayah",
                    category = "Bahasa & Bahasa",
                    description = "Teknik penulisan esai, analisis puisi, dan penyusunan tata bahasa Indonesia yang santun.",
                    pdfFileName = "bahasa_indonesia.pdf",
                    totalPages = 130
                )
            )
            bookDao.insertAll(initialList)
        }
    }
}
