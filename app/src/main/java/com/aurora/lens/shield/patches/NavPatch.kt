package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class NavPatch : ShieldPatch {
    override val id = "Navigator"

    private fun String.js(): String =
        "\"${this.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")}\""

    private fun List<String>.js(): String =
        joinToString(",", "[", "]") { "\"$it\"" }

    override fun generate(p: ShieldProfile): String = """
seal(navigator, {
    userAgent:${p.userAgent.js()},
    platform:${p.platform.js()},
    vendor:${p.vendor.js()},
    vendorSub:${p.vendorSub.js()},
    product:${p.product.js()},
    productSub:${p.productSub.js()},
    appName:${p.appName.js()},
    appVersion:${p.appVersion.js()},
    appCodeName:${p.appCodeName.js()},
    language:${p.language.js()},
    languages:${p.languages.js()},
    hardwareConcurrency:${p.hardwareConcurrency},
    deviceMemory:${p.deviceMemory},
    maxTouchPoints:${p.maxTouchPoints},
    cookieEnabled:true,
    onLine:true,
    webdriver:false,
    doNotTrack:null,
});

try{DP(navigator,'plugins',{get:()=>FR(Object.create(PluginArray.prototype)),configurable:false,enumerable:true});}catch(_){}
try{DP(navigator,'mimeTypes',{get:()=>FR(Object.create(MimeTypeArray.prototype)),configurable:false,enumerable:true});}catch(_){}

try{
const fc=Object.create((navigator.connection||{}).__proto__||{});
seal(fc,{effectiveType:${p.effectiveType.js()},downlink:${p.downlink},rtt:${p.rtt},saveData:false,type:'wifi'});
DP(navigator,'connection',{get:()=>fc,configurable:false,enumerable:true});
}catch(_){}

navigator.getBattery=()=>Promise.resolve(FR({charging:true,chargingTime:0,dischargingTime:1/0,level:1,addEventListener:()=>{}}));

DP(navigator,'mediaDevices',{get:()=>({enumerateDevices:()=>Promise.resolve([]),getUserMedia:()=>Promise.reject(new DOMException('','NotAllowedError')),addEventListener:()=>{}}),configurable:false,enumerable:true});

DP(navigator,'geolocation',{get:()=>({getCurrentPosition:(_,e)=>{if(e)e({code:1,message:'denied'});},watchPosition:()=>0,clearWatch:()=>{}}),configurable:false,enumerable:true});

DP(navigator,'serviceWorker',{get:()=>({register:()=>Promise.reject(new DOMException('','NotSupportedError')),getRegistration:()=>Promise.resolve(void 0),getRegistrations:()=>Promise.resolve([]),ready:Promise.reject(),controller:null,addEventListener:()=>{}}),configurable:false,enumerable:true});

DP(navigator,'clipboard',{get:()=>({read:()=>Promise.reject(),readText:()=>Promise.reject(),write:()=>Promise.resolve(),writeText:()=>Promise.resolve()}),configurable:false,enumerable:true});

['bluetooth','serial','usb','xr','hid','presentation','keyboard'].forEach(k=>{try{DP(navigator,k,{get:()=>void 0,configurable:false,enumerable:true});}catch(_){}});

navigator.getGamepads=()=>[null,null,null,null];
navigator.vibrate=()=>false;

if(navigator.credentials){
    navigator.credentials.get=()=>Promise.resolve(null);
    navigator.credentials.store=()=>Promise.resolve();
    navigator.credentials.create=()=>Promise.resolve(null);
}

if(navigator.storage){
    const oe=navigator.storage.estimate.bind(navigator.storage);
    navigator.storage.estimate=()=>Promise.resolve({quota:107374182400,usage:0});
}
""".trimIndent()
}
