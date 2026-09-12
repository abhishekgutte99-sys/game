package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SacredAudioEngine {
    private const val SAMPLE_RATE = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playTempleBell() {
        scope.launch {
            val durationMs = 800
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freq1 = 852.0
            val freq2 = 1280.0
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val envelope = kotlin.math.exp(-3.5 * t)
                val s1 = sin(2.0 * Math.PI * freq1 * t)
                val s2 = 0.5 * sin(2.0 * Math.PI * freq2 * t)
                val sample = ((s1 + s2) * envelope * 0.7 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playModakChime() {
        scope.launch {
            val durationMs = 180
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freq1 = 1046.5 // C6
            val freq2 = 1318.5 // E6
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val s = sin(2.0 * Math.PI * (if (i < numSamples / 2) freq1 else freq2) * t)
                val sample = (s * envelope * 0.5 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playJump() {
        scope.launch {
            val durationMs = 140
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val currentFreq = 300.0 + progress * 450.0
                val s = sin(2.0 * Math.PI * currentFreq * t)
                val envelope = 1.0 - progress
                val sample = (s * envelope * 0.4 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playPowerUp() {
        scope.launch {
            val durationMs = 450
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freqs = listOf(523.25, 659.25, 783.99, 1046.50) // C-E-G-C chord
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val noteIdx = ((i.toDouble() / numSamples) * freqs.size).toInt().coerceIn(0, freqs.size - 1)
                val freq = freqs[noteIdx]
                val s = sin(2.0 * Math.PI * freq * t)
                val envelope = kotlin.math.exp(-1.5 * (i % (numSamples / freqs.size)).toDouble() / (SAMPLE_RATE * 0.1))
                val sample = (s * envelope * 0.6 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playOmResonance() {
        scope.launch {
            val durationMs = 1200
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val baseFreq = 136.1 // Cosmic Om frequency
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = sin(Math.PI * (i.toDouble() / numSamples)) // Gentle fade-in and fade-out
                val s1 = sin(2.0 * Math.PI * baseFreq * t)
                val s2 = 0.4 * sin(2.0 * Math.PI * (baseFreq * 2) * t)
                val s3 = 0.2 * sin(2.0 * Math.PI * (baseFreq * 3) * t)
                val sample = ((s1 + s2 + s3) * env * 0.6 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    fun playObstacleThud() {
        scope.launch {
            val durationMs = 200
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val freq = 120.0 - (i.toDouble() / numSamples) * 60.0
                val s = sin(2.0 * Math.PI * freq * t)
                val envelope = kotlin.math.exp(-10.0 * t)
                val sample = (s * envelope * 0.7 * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // release after playback
            scope.launch {
                val delayMs = ((buffer.size.toDouble() / SAMPLE_RATE) * 1000).toLong() + 100
                kotlinx.coroutines.delay(delayMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // gracefully ignore if device audio unavailable
        }
    }
}
