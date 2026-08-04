package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsEngine(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    var currentSentenceIndex = 0
        private set

    private var sentences: List<String> = emptyList()
    var isPlaying = false
        private set

    var playbackRate = 1.0f
        private set

    var voiceGender = "FEMALE"
        private set

    private var onSentenceHighlightListener: ((Int) -> Unit)? = null

    fun setHighlightListener(listener: (Int) -> Unit) {
        onSentenceHighlightListener = listener
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (isPlaying && currentSentenceIndex < sentences.size - 1) {
                        currentSentenceIndex++
                        playSentence(currentSentenceIndex)
                    } else {
                        isPlaying = false
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isPlaying = false
                }
            })
        }
    }

    fun loadSentences(paragraphText: String) {
        sentences = paragraphText
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        currentSentenceIndex = 0
    }

    fun setRate(rate: Float) {
        playbackRate = rate
        tts?.setSpeechRate(rate)
    }

    fun setVoiceType(gender: String) {
        voiceGender = gender
        if (gender == "MALE") {
            tts?.setPitch(0.85f)
        } else {
            tts?.setPitch(1.15f)
        }
    }

    fun play() {
        if (sentences.isEmpty()) return
        isPlaying = true
        playSentence(currentSentenceIndex)
    }

    private fun playSentence(index: Int) {
        if (index in sentences.indices) {
            onSentenceHighlightListener?.invoke(index)
            val text = sentences[index]
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "sentence_$index"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
        }
    }

    fun pause() {
        isPlaying = false
        tts?.stop()
    }

    fun repeatParagraph() {
        tts?.stop()
        currentSentenceIndex = 0
        play()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
