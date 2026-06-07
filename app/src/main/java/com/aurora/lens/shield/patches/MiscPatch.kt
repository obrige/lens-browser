package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class MiscPatch : ShieldPatch {
    override val id = "Misc"

    override fun generate(p: ShieldProfile): String = """
try{DP(document,'fonts',{
    get:()=>({
        values:()=>[].values(),
        entries:()=>[].entries(),
        forEach:()=>{},
        add:()=>{},
        delete:()=>{},
        clear:()=>{},
        ready:Promise.resolve(),
        status:'loaded',
        size:0,
        [Symbol.iterator]:()=>[].values(),
    }),
    configurable:false,
    enumerable:true
});}catch(_){}

try{DP(window,'name',{value:'',writable:true,configurable:false});}catch(_){}

try{if(window.Notification){
    window.Notification.requestPermission=()=>Promise.resolve('denied');
    DP(window.Notification,'permission',{get:()=>'denied',configurable:false});
}}catch(_){}

try{DP(window,'PaymentRequest',{value:void 0,configurable:false,writable:false});}catch(_){}
try{DP(window,'SpeechSynthesisUtterance',{value:void 0,configurable:false,writable:false});}catch(_){}
try{DP(window,'webkitSpeechRecognition',{value:void 0,configurable:false,writable:false});}catch(_){}
try{DP(window,'BroadcastChannel',{value:void 0,configurable:false,writable:false});}catch(_){}
try{DP(window,'SharedWorker',{value:void 0,configurable:false,writable:false});}catch(_){}

try{if(console.memory)DP(console,'memory',{
    get:()=>({
        jsHeapSizeLimit:2172649472,
        totalJSHeapSize:42000000,
        usedJSHeapSize:38000000
    }),
    configurable:false
});}catch(_){}
""".trimIndent()
}
