package com.aurora.lens.shield.patches

import com.aurora.lens.shield.ShieldPatch
import com.aurora.lens.shield.ShieldProfile

class WebGLPatch : ShieldPatch {
    override val id = "WebGL"

    private fun String.js(): String =
        "\"${this.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")}\""

    override fun generate(p: ShieldProfile): String = """
(function(){
const S=new Map([
    [37445,${p.glVendor.js()}],
    [37446,${p.glRenderer.js()}],
    [7936,'WebGL GLSL ES'],
    [7937,${p.glVersion.js()}],
    [7938,'WebGL'],
    [35724,'WebGL GLSL ES'],
    [3415,0],[3414,0],[3413,0],[3412,0],
    [3411,24],[3410,8],
    [34024,4096],[3379,16384],[3386,32],[34076,4096],
    [35661,16],[34930,32],[35660,4096],[36349,1024],
    [36348,256],[34921,8],[35658,31],
]);
const pt=(pr)=>{
    const o=pr.getParameter;
    pr.getParameter=function(pn){
        return S.has(pn)?S.get(pn):o.call(this,pn);
    };
};
if(typeof WebGLRenderingContext!=='undefined'){
    pt(WebGLRenderingContext.prototype);
    const oE=WebGLRenderingContext.prototype.getSupportedExtensions;
    WebGLRenderingContext.prototype.getSupportedExtensions=function(){
        return (oE.call(this)||[]).filter(e=>
            !['WEBGL_debug_renderer_info','WEBGL_debug_shaders','EXT_disjoint_timer_query'].includes(e)
        );
    };
    const oG=WebGLRenderingContext.prototype.getExtension;
    WebGLRenderingContext.prototype.getExtension=function(n){
        return ['WEBGL_debug_renderer_info','WEBGL_debug_shaders'].includes(n)?null:oG.call(this,n);
    };
    FR(WebGLRenderingContext.prototype.getParameter);
    FR(WebGLRenderingContext.prototype.getSupportedExtensions);
}
if(typeof WebGL2RenderingContext!=='undefined'){
    pt(WebGL2RenderingContext.prototype);
    FR(WebGL2RenderingContext.prototype.getParameter);
}
})();
""".trimIndent()
}
