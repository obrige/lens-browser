package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class CanvasPatch : ShieldPatch {
    override val id = "Canvas"

    override fun generate(p: ShieldProfile): String = """
(function(){
const rnd=()=>(Math.random()<0.5?-1:1)*(Math.random()<0.01?2:0);
const noise=(d)=>{
    for(let i=0;i<d.length;i+=4){
        d[i]=Math.min(255,Math.max(0,d[i]+rnd()));
        d[i+1]=Math.min(255,Math.max(0,d[i+1]+rnd()));
        d[i+2]=Math.min(255,Math.max(0,d[i+2]+rnd()));
    }
    return d;
};
const oGID=CanvasRenderingContext2D.prototype.getImageData;
CanvasRenderingContext2D.prototype.getImageData=function(x,y,w,h){
    const r=oGID.call(this,x,y,w,h);noise(r.data);return r;
};
const oTD=HTMLCanvasElement.prototype.toDataURL;
HTMLCanvasElement.prototype.toDataURL=function(){
    const c=this.getContext('2d');
    if(c){const d=c.getImageData(0,0,this.width,this.height);noise(d.data);c.putImageData(d,0,0);}
    return oTD.apply(this,arguments);
};
const oTB=HTMLCanvasElement.prototype.toBlob;
HTMLCanvasElement.prototype.toBlob=function(cb,ty,q){
    const c=this.getContext('2d');
    if(c){const d=c.getImageData(0,0,this.width,this.height);noise(d.data);c.putImageData(d,0,0);}
    return oTB.call(this,cb,ty,q);
};
const oMT=CanvasRenderingContext2D.prototype.measureText;
CanvasRenderingContext2D.prototype.measureText=function(t){
    const m=oMT.call(this,t);
    const n=(Math.random()-0.5)*0.3;
    DP(m,'width',{value:m.width+n,writable:false,configurable:false,enumerable:true});
    return m;
};
FR(CanvasRenderingContext2D.prototype.getImageData);
FR(HTMLCanvasElement.prototype.toDataURL);
FR(HTMLCanvasElement.prototype.toBlob);
})();
""".trimIndent()
}
