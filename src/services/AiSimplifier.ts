/**
 * AiSimplifier.ts
 * Membaca teks materi PDF/buku dan mengubahnya menjadi narasi gaya anak SMP
 * dengan frasa ramah seperti "jadi gini", "paham ya", "contohnya".
 */

export interface SimplifiedParagraph {
  originalText: string;
  simplifiedText: string;
  keySentence: string;
}

export class AiSimplifier {
  /**
   * Menyederhanakan konten teks menjadi narasi gaya anak SMP
   */
  public static simplifyContent(rawText: string, topicTitle: string = 'Materi Buku'): SimplifiedParagraph[] {
    const cleanText = rawText.trim();
    if (!cleanText) {
      return [
        {
          originalText: '',
          simplifiedText: 'Jadi gini, materi di bab ini simpel banget. Pilih halaman yang ingin dibaca ya, paham ya?',
          keySentence: 'Pilih halaman yang ingin dibaca.',
        },
      ];
    }

    // Algoritma pembagian paragraf & simplifikasi lokal offline
    const sentences = cleanText
      .split(/(?<=[.!?])\s+/)
      .filter((s) => s.length > 5);

    const paragraphs: SimplifiedParagraph[] = [];
    const chunkSize = 3;

    for (let i = 0; i < sentences.length; i += chunkSize) {
      const chunk = sentences.slice(i, i + chunkSize);
      const combined = chunk.join(' ');

      // Penyisipan narasi komunikatif khas "GuruAI"
      let narasi = combined;
      if (i === 0) {
        narasi = `Jadi gini, kalau kita bahas tentang ${topicTitle}, intinya gampang banget. ${combined} Contohnya bisa kita lihat dalam kehidupan sehari-hari, paham ya?`;
      } else if (i % 2 === 0) {
        narasi = `Nah terus, ${combined} Paham ya kawan-kawan, konsep ini penting banget buat dipahami!`;
      } else {
        narasi = `Lanjut lagi, ${combined} Contohnya kalau di dunia nyata, hal ini yang bikin semuanya berjalan seimbang.`;
      }

      paragraphs.push({
        originalText: combined,
        simplifiedText: narasi,
        keySentence: chunk[0] || combined,
      });
    }

    return paragraphs;
  }

  /**
   * Menggenerasi 10 Pilihan Ganda & 5 Esai dari teks PDF
   */
  public static generateQuiz(pdfText: string) {
    const sampleSentences = pdfText
      .split(/(?<=[.!?])\s+/)
      .filter((s) => s.length > 10);

    const multipleChoice = Array.from({ length: 10 }).map((_, idx) => {
      const topic = sampleSentences[idx % sampleSentences.length] || `Konsep Penting Halaman ${idx + 1}`;
      return {
        id: `mc_${idx + 1}`,
        question: `Soal ${idx + 1}: Menurut isi teks materi, apakah frasa kunci berikut bernilai tepat: "${topic.substring(0, 60)}..."?`,
        options: [
          'A. Sesuai dengan fakta utama materi',
          'B. Bertentangan dengan teori dasar',
          'C. Hanya berlaku pada kondisi tertentu',
          'D. Tidak dijelaskan dalam buku',
        ],
        correctAnswerIndex: 0,
        explanation: `Sesuai dengan paragraf materi: "${topic.substring(0, 80)}"`,
      };
    });

    const essayQuestions = Array.from({ length: 5 }).map((_, idx) => {
      return {
        id: `essay_${idx + 1}`,
        question: `Soal Esai ${idx + 1}: Jelaskan dengan bahasamu sendiri (gaya anak SMP) mengenai poin penting ke-${idx + 1} dari bab ini dan berikan contoh penerapannya!`,
        hintAnswer: `Gunakan frasa penjelasan runtut seperti: "Jadi gini, poin ini menjelaskan bahwa..."`,
      };
    });

    return { multipleChoice, essayQuestions };
  }
}
