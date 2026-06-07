package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class RtcPatch : ShieldPatch {
    override val id = "WebRTC"

    override fun generate(p: ShieldProfile): String = """
const block=()=>{throw new DOMException('WebRTC disabled','NotSupportedError');};
['RTCPeerConnection','webkitRTCPeerConnection','mozRTCPeerConnection'].forEach(k=>{
    try{DP(window,k,{value:block,writable:false,configurable:false});}catch(_){}
});
['RTCDataChannel','RTCSessionDescription','RTCIceCandidate','RTCDataChannelEvent','RTCDtlsTransport','RTCIceTransport','RTCCertificate','RTCRtpTransceiver','RTCRtpReceiver','RTCRtpSender'].forEach(k=>{
    try{DP(window,k,{value:void 0,writable:false,configurable:false});}catch(_){}
});
""".trimIndent()
}
