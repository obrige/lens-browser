package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class PerfPatch : ShieldPatch {
    override val id = "Performance"

    override fun generate(p: ShieldProfile): String = """
if(performance){
try{DP(performance,'memory',{
    get:()=>({
        jsHeapSizeLimit:2172649472,
        totalJSHeapSize:42000000,
        usedJSHeapSize:38000000
    }),
    configurable:false,
    enumerable:true
});}catch(_){}
try{if(performance.navigation)
    seal(performance.navigation,{type:0,redirectCount:0});
}catch(_){}
}
""".trimIndent()
}
