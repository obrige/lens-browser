package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class AudioPatch : ShieldPatch {
    override val id = "AudioContext"

    override fun generate(p: ShieldProfile): String = """
(function(){
const an=(d)=>{for(let i=0;i<d.length;i++)d[i]+=(Math.random()-0.5)*2e-8;return d;};
const pa=(pr)=>{
    if(!pr)return;
    ['getFloatTimeDomainData','getFloatFrequencyData','getByteTimeDomainData','getByteFrequencyData'].forEach(m=>{
        const o=pr[m];
        if(o)pr[m]=function(a){o.call(this,a);an(a);};
    });
};
if(typeof AnalyserNode!=='undefined')pa(AnalyserNode.prototype);
const pc=(pr)=>{
    if(!pr||!pr.createAnalyser)return;
    const o=pr.createAnalyser;
    pr.createAnalyser=function(){
        const n=o.call(this);
        pa(Object.getPrototypeOf(n));
        return n;
    };
};
if(typeof AudioContext!=='undefined')pc(AudioContext.prototype);
if(typeof webkitAudioContext!=='undefined')pc(webkitAudioContext.prototype);
if(typeof OfflineAudioContext!=='undefined'){
    const OO=OfflineAudioContext;
    window.OfflineAudioContext=function(c,l,sr){
        if(l>256)l=256;
        if(sr>44100)sr=44100;
        return new OO(c,l,sr);
    };
    window.OfflineAudioContext.prototype=OO.prototype;
    for(const k of Object.getOwnPropertyNames(OO))
        try{window.OfflineAudioContext[k]=OO[k];}catch(_){}
}
})();
""".trimIndent()
}
