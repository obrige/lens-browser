package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class TimezonePatch : ShieldPatch {
    override val id = "Timezone"

    private fun String.js(): String =
        "\"${this.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")}\""

    override fun generate(p: ShieldProfile): String = """
(function(){
const TZ=${p.timezone.js()};
try{
    const O=Intl.DateTimeFormat;
    Intl.DateTimeFormat=function(l,o){
        const op=o?Object.assign({},o):{};
        op.timeZone=TZ;
        return new O(l,op);
    };
    Intl.DateTimeFormat.prototype=O.prototype;
    for(const k of Object.getOwnPropertyNames(O))
        try{Intl.DateTimeFormat[k]=O[k];}catch(_){}
    Intl.DateTimeFormat.supportedLocalesOf=O.supportedLocalesOf.bind(O);
}catch(_){}
try{
    const d=new Date();
    const utc=d.getTime()+d.getTimezoneOffset()*60000;
    let off=300;
    try{
        const s=new Intl.DateTimeFormat('en-US',{timeZone:TZ,timeStyle:'short'}).format(d);
        off=-Math.round((utc-new Date(s).getTime())/60000);
    }catch(_){}
    Date.prototype.getTimezoneOffset=function(){return off;};
    FR(Date.prototype.getTimezoneOffset);
}catch(_){}
})();
""".trimIndent()
}
