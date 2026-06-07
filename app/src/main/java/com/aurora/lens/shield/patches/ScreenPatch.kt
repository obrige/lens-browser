package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class ScreenPatch : ShieldPatch {
    override val id = "Screen+Window"

    override fun generate(p: ShieldProfile): String = """
seal(screen,{
    width:${p.screenWidth},
    height:${p.screenHeight},
    availWidth:${p.availWidth},
    availHeight:${p.availHeight},
    colorDepth:${p.colorDepth},
    pixelDepth:${p.pixelDepth}
});
DP(screen,'orientation',{get:()=>FR({type:'landscape-primary',angle:0,onchange:null}),configurable:false,enumerable:true});
seal(window,{devicePixelRatio:${p.devicePixelRatio}});
seal(window,{
    innerWidth:${p.innerWidth},
    innerHeight:${p.innerHeight},
    outerWidth:${p.outerWidth},
    outerHeight:${p.outerHeight}
});
['locationbar','menubar','personalbar','scrollbars','statusbar','toolbar'].forEach(b=>{
    try{DP(window,b,{get:()=>({visible:true}),configurable:false,enumerable:true});}catch(_){}
});
""".trimIndent()
}
