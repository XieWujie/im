package com.rtp.voice

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class RtpAudioTrack {

    private val trackPlayer:AudioTrack

    init {
        val bufSize = AudioTrack.getMinBufferSize(8000
            ,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT)
        trackPlayer = AudioTrack(AudioManager.STREAM_VOICE_CALL,8000,
        AudioFormat.CHANNEL_CONFIGURATION_STEREO,
        AudioFormat.ENCODING_PCM_16BIT,bufSize,AudioTrack.MODE_STREAM)
        trackPlayer.play()
    }

    fun write(byte:ByteArray){
        trackPlayer.write(byte,0,byte.size)
    }

    fun release(){
        trackPlayer.stop()
        trackPlayer.release()
    }
}