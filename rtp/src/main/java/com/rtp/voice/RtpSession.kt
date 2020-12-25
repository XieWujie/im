package com.rtp.voice

import com.rtp.jlibrtp.DataFrame
import com.rtp.jlibrtp.Participant
import com.rtp.jlibrtp.RTPAppIntf
import com.rtp.jlibrtp.RTPSession
import java.net.DatagramSocket

object RtpSession : RTPAppIntf, Runnable {
    val rtpSession: RTPSession
    private val track = RtpAudioTrack()
    private val record = RtpAudioRecord()

    private var callListener:((Participant)->Unit)? = null
    private var byeListener:((Participant)->Unit)? = null


    @Volatile
    private var isReleased = false

    override fun receiveData(frame: DataFrame, p: Participant) {
        val s = String(frame.concatenatedData)
        track.write(frame.concatenatedData)
    }

    override fun userEvent(type: Int, participant: Array<Participant>) {
        when(type){
            1->participant?.forEach {
                byeListener?.invoke(it)
            }
            2-> participant.forEach {
                callListener?.invoke(it)
            }
        }
    }

    override fun frameSize(payloadType: Int): Int {
        return 1
    }

    override fun run() {
        while (!isReleased) {
            val b = ByteArray(1024)
            record.read(b)
            rtpSession.sendData(b)
        }
    }

    fun start(){
        Thread(this).start()
    }


    init {
        var rtpSocket: DatagramSocket? = null
        var rtcpSocket: DatagramSocket? = null
        try {
            rtpSocket = DatagramSocket(8002)
            rtcpSocket = DatagramSocket(8003)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        rtpSession = RTPSession(rtpSocket, rtcpSocket).apply {
            RTPSessionRegister(this@RtpSession, null, null)
        }


        //建立会话

    }

    fun addParticipant(p:Participant) {
        rtpSession.addParticipant(p)
    }
}