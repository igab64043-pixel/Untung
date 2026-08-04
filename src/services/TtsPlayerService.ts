/**
 * TtsPlayerService.ts
 * Mengelola Text-To-Speech (TTS) offline, kontrol suara (Pria/Wanita),
 * kecepatan (0.8x, 1x, 1.5x), dan sinkronisasi highlight kalimat.
 */

export type VoiceGender = 'MALE' | 'FEMALE' | 'NEUTRAL';
export type PlaybackRate = 0.8 | 1.0 | 1.5;

export interface TtsSettings {
  gender: VoiceGender;
  rate: PlaybackRate;
  pitch: number;
}

class TtsPlayerService {
  private isPlaying: boolean = false;
  private currentSentenceIndex: number = 0;
  private sentences: string[] = [];
  private settings: TtsSettings = {
    gender: 'FEMALE',
    rate: 1.0,
    pitch: 1.0,
  };

  private onSentenceHighlightCallback: ((index: number) => void) | null = null;

  public setCallback(cb: (index: number) => void) {
    this.onSentenceHighlightCallback = cb;
  }

  public setSettings(newSettings: Partial<TtsSettings>) {
    this.settings = { ...this.settings, ...newSettings };
    console.log('Updated TTS Settings:', this.settings);
  }

  public loadParagraph(text: string) {
    this.sentences = text
      .split(/(?<=[.!?])\s+/)
      .map((s) => s.trim())
      .filter(Boolean);
    this.currentSentenceIndex = 0;
  }

  public play() {
    if (this.sentences.length === 0) return;
    this.isPlaying = true;
    console.log(`[TTS] Playing sentence ${this.currentSentenceIndex + 1}/${this.sentences.length} at rate ${this.settings.rate}x`);

    if (this.onSentenceHighlightCallback) {
      this.onSentenceHighlightCallback(this.currentSentenceIndex);
    }
  }

  public pause() {
    this.isPlaying = false;
    console.log('[TTS] Paused playback');
  }

  public repeatParagraph() {
    this.currentSentenceIndex = 0;
    this.play();
  }

  public nextSentence() {
    if (this.currentSentenceIndex < this.sentences.length - 1) {
      this.currentSentenceIndex++;
      this.play();
    } else {
      this.isPlaying = false;
      console.log('[TTS] Reached end of paragraph');
    }
  }

  public exportAudioAsTextFile(): string {
    return this.sentences.join('\n\n');
  }
}

export const ttsPlayerService = new TtsPlayerService();
