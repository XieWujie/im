package com.vlog.conversation.phone

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription


data class Model(val sessionDescription: SessionDescription,val iceCandidate: IceCandidate)