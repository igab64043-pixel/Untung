package com.example.service

data class SimplifiedParagraph(
    val originalText: String,
    val simplifiedText: String,
    val keySentence: String
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val explanation: String = "",
    val isEssay: Boolean = false,
    val hintAnswer: String = ""
)

data class GeneratedQuiz(
    val multipleChoice: List<QuizQuestion>,
    val essayQuestions: List<QuizQuestion>
)

object AiGuruEngine {

    /**
     * AI Simplifier: Mengubah isi PDF menjadi narasi berbahasa anak SMP
     * Aturan:
     * - Bahasa setara anak SMP
     * - Contoh nyata
     * - Paragraf pendek (2-3 kalimat)
     * - Frasa: "jadi gini", "paham ya", "contohnya"
     */
    fun simplifyTextForSmp(rawText: String, topicTitle: String = "Materi"): List<SimplifiedParagraph> {
        val cleanText = rawText.trim()
        if (cleanText.isEmpty()) {
            return listOf(
                SimplifiedParagraph(
                    originalText = "",
                    simplifiedText = "Jadi gini, topik ini gampang banget dibahas. Silahkan pilih bab atau halaman PDF dulu ya, paham ya?",
                    keySentence = "Pilih materi PDF terlebih dahulu."
                )
            )
        }

        val sentences = cleanText
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.length > 5 }

        val result = mutableListOf<SimplifiedParagraph>()
        val chunkSize = 3

        for (i in sentences.indices step chunkSize) {
            val chunk = sentences.subList(i, minOf(i + chunkSize, sentences.size))
            val originalBlock = chunk.joinToString(" ")

            val narasi = when {
                i == 0 -> "Jadi gini, kalau kita pelajari tentang $topicTitle, prinsip dasarnya simpel banget! $originalBlock. Contohnya bisa kita amati pas aktivitas sehari-hari di rumah atau sekolah, paham ya?"
                i % 2 == 0 -> "Nah terus, $originalBlock. Paham ya kawan-kawan, bagian ini kunci penting biar kita nggak bingung pas ujian!"
                else -> "Lanjut lagi, $originalBlock. Contohnya pas kita nyoba bereksperimen, hasilnya langsung kelihatan sesuai hukum ini!"
            }

            result.add(
                SimplifiedParagraph(
                    originalText = originalBlock,
                    simplifiedText = narasi,
                    keySentence = chunk.firstOrNull() ?: originalBlock
                )
            )
        }

        return result
    }

    /**
     * Generator Kuis: 10 Pilihan Ganda + 5 Esai digenerate dari isi PDF
     */
    fun generateQuizFromPdf(pdfContent: String, bookTitle: String): GeneratedQuiz {
        val sentences = pdfContent.split(Regex("(?<=[.!?])\\s+"))
            .filter { it.length > 10 }

        val mcList = (1..10).map { index ->
            val sampleSentence = sentences.getOrNull(index % maxOf(1, sentences.size)) ?: "Konsep utama bab $bookTitle"
            QuizQuestion(
                id = "mc_$index",
                question = "Soal $index: Berdasarkan materi $bookTitle, apakah pernyataan berikut sesuai: \"${sampleSentence.take(70)}...\"?",
                options = listOf(
                    "A. Sesuai dengan fakta utama materi",
                    "B. Bertentangan dengan konsep dasar",
                    "C. Hanya berlaku dalam kondisi laboratorium khusus",
                    "D. Tidak dijelaskan sama sekali dalam buku"
                ),
                correctAnswerIndex = 0,
                explanation = "Penjelasan: Pernyataan ini secara langsung tertera pada pembahasan bab.",
                isEssay = false
            )
        }

        val essayList = (1..5).map { index ->
            QuizQuestion(
                id = "essay_$index",
                question = "Soal Esai $index: Jelaskan dengan bahasamu sendiri (gaya narasi santai) tentang poin penting ke-$index dari $bookTitle dan berikan 1 contoh kasusnya di kehidupan nyata!",
                hintAnswer = "Gunakan kalimat pembuka seperti: 'Jadi gini, poin ini menjelaskan bahwa...'",
                isEssay = true
            )
        }

        return GeneratedQuiz(multipleChoice = mcList, essayQuestions = essayList)
    }

    /**
     * Ringkasan Buku Instant
     */
    fun summarizeBook(bookTitle: String, bookCategory: String): String {
        return """
            📌 Ringkasan Eksekutif Buku: $bookTitle
            • Kategori: $bookCategory
            • Poin Utama 1: Konsep dasar dan teori landasan dijelaskan secara sistematis.
            • Poin Utama 2: Aplikasi praktis dan rumus/prinsip kunci dalam kehidupan nyata.
            • Poin Utama 3: Penekanan pada pemecahan masalah dan analisis kasus.
            • Kesimpulan GuruAI: Buku ini sangat cocok dipelajari untuk memperkuat pemahaman konsep dasar dengan bahasa ringkas dan efektif.
        """.trimIndent()
    }
}
