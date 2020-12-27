package com.vlog.conversation.phone

import android.content.Context
import android.util.Log
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.connect.MessageSend
import com.vlog.database.Message
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions


private const val TAG = "SessionInitial"

class SessionInitial(private val fromType:Int,private val conversationId:Int,private val  context: Context) {

    private lateinit var localSurfaceView: SurfaceViewRenderer
    private lateinit var remoteSurfaceView: SurfaceViewRenderer
    private lateinit var eglBase: EglBase
    private lateinit var peerConnectionFactory: PeerConnectionFactory

    private lateinit var channel: DataChannel
    private lateinit var videoTrack: VideoTrack
    private lateinit var audioTrack: AudioTrack
    private lateinit var peerConnection: PeerConnection
    private lateinit var streamList: List<String>
    private lateinit var observer: MySdpObserver

    private lateinit var iceServers: ArrayList<IceServer>


     fun createPeerConnection() {
        //Initialising PeerConnectionFactory
        val initializationOptions = InitializationOptions.builder(context.applicationContext)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        //创建EglBase对象
        eglBase = EglBase.create()
        val options = PeerConnectionFactory.Options()
        options.disableEncryption = true
        options.disableNetworkMonitor = true
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setOptions(options)
            .createPeerConnectionFactory()
        // 配置STUN穿透服务器  转发服务器
        iceServers = ArrayList()
        val iceServer: IceServer = IceServer.builder(PhoneConstant.STUN).createIceServer()
        iceServers.add(iceServer)
        streamList = ArrayList()
        val configuration = RTCConfiguration(iceServers)
        val connectionObserver: PeerConnectionObserver = getObserver()
        peerConnection =
            peerConnectionFactory.createPeerConnection(configuration, connectionObserver)!!


        /*
        DataChannel.Init 可配参数说明：
        ordered：是否保证顺序传输；
        maxRetransmitTimeMs：重传允许的最长时间；
        maxRetransmits：重传允许的最大次数；
        */
        val init = DataChannel.Init()
        channel = peerConnection.createDataChannel(PhoneConstant.CHANNEL, init)
        val channelObserver = DateChannelObserver()
        connectionObserver.setObserver(channelObserver)
        initObserver()
    }



    fun setDes(description: SessionDescription){
        peerConnection.setRemoteDescription(observer,description)
        createAnswer()
    }


    private fun initObserver() {
        observer = object : MySdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                //将会话描述设置在本地
                sessionDescription ?: throw RuntimeException("sessionDescription == null")
                peerConnection.setLocalDescription(this, sessionDescription)
                val localDescription = peerConnection.localDescription
                val type = localDescription.type
                Log.e(TAG, "onCreateSuccess ==  type == $type")
                //接下来使用之前的WebSocket实例将offer发送给服务器
                when (type) {
                    SessionDescription.Type.OFFER -> offer(sessionDescription)
                    SessionDescription.Type.ANSWER -> answer(sessionDescription)
                    SessionDescription.Type.PRANSWER -> {
                    }
                }
            }
        }
    }


    private fun getObserver(): PeerConnectionObserver {
        return object : PeerConnectionObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate?) {
                super.onIceCandidate(iceCandidate)
                if (iceCandidate != null) {
                    setIceCandidate(iceCandidate)
                }
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                Log.d(TAG, "onAddStream : $mediaStream")
                val videoTracks = mediaStream.videoTracks
                if (videoTracks != null && videoTracks.size > 0) {
                    val videoTrack = videoTracks[0]
                    videoTrack?.addSink(remoteSurfaceView)
                }
                val audioTracks = mediaStream.audioTracks
                if (audioTracks != null && audioTracks.size > 0) {
                    val audioTrack = audioTracks[0]
                    audioTrack?.setVolume(PhoneConstant.VOLUME.toDouble())
                }
            }
        }
    }

    fun addIce(iceCandidate: IceCandidate){
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun attachView(local: SurfaceViewRenderer,remote:SurfaceViewRenderer,videoCapturer: VideoCapturer){
        initSurfaceView(remote)
        initSurfaceView(local)
        startLocalAudioCapture()
        startLocalVideoCapture(localSurfaceView,videoCapturer)
    }

    private fun startLocalVideoCapture(localSurfaceView: SurfaceViewRenderer,videoCapturer: VideoCapturer) {
        val videoSource = peerConnectionFactory.createVideoSource(true)
        val surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            eglBase.eglBaseContext
        )
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(
            PhoneConstant.VIDEO_RESOLUTION_WIDTH,
            PhoneConstant.VIDEO_RESOLUTION_HEIGHT,
            PhoneConstant.VIDEO_FPS
        ) // width, height, frame per second
        videoTrack = peerConnectionFactory.createVideoTrack(
            PhoneConstant.VIDEO_TRACK_ID,
            videoSource
        )
        videoTrack.addSink(localSurfaceView)
        val localMediaStream =
            peerConnectionFactory.createLocalMediaStream(PhoneConstant.LOCAL_VIDEO_STREAM)
        localMediaStream.addTrack(videoTrack)
        peerConnection.addTrack(videoTrack, streamList)
        peerConnection.addStream(localMediaStream)
    }

    private fun startLocalAudioCapture() {
        //语音
        val audioConstraints = MediaConstraints()
        //回声消除
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation",
                "true"
            )
        )
        //自动增益
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        //高音过滤
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        //噪音处理
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googNoiseSuppression",
                "true"
            )
        )
        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        audioTrack = peerConnectionFactory.createAudioTrack(
            PhoneConstant.AUDIO_TRACK_ID,
            audioSource
        )
        val localMediaStream =
            peerConnectionFactory.createLocalMediaStream(PhoneConstant.LOCAL_AUDIO_STREAM)
        localMediaStream.addTrack(audioTrack)
        audioTrack.setVolume(PhoneConstant.VOLUME.toDouble())
        peerConnection.addTrack(audioTrack, streamList)
        peerConnection.addStream(localMediaStream)
    }

    private fun initSurfaceView(localSurfaceView: SurfaceViewRenderer) {
        localSurfaceView.init(eglBase.eglBaseContext, null)
        localSurfaceView.setMirror(true)
        localSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        localSurfaceView.keepScreenOn = true
        localSurfaceView.setZOrderMediaOverlay(true)
        localSurfaceView.setEnableHardwareScaler(false)
    }

    fun createOffer() {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peerConnection.createOffer(observer, mediaConstraints)
    }

     fun createAnswer() {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peerConnection.createAnswer(observer, mediaConstraints)
    }

    /**
     * 应答
     *
     * @param sdpDescription
     */
    private fun answer(sdpDescription: SessionDescription) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_DES_OFFER,g.toJson(sdpDescription),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }

    /**
     * 呼叫
     *
     * @param sdpDescription
     */
    private fun offer(sdpDescription: SessionDescription) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_DES_OFFER,g.toJson(sdpDescription),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }

    /**
     * 呼叫
     *
     * @param iceCandidate
     */
    private fun setIceCandidate(iceCandidate: IceCandidate) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_ICE_OFFER,g.toJson(iceCandidate),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }


}