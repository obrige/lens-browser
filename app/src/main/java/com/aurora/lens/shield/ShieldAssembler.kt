package com.aurora.lens.shield

import com.aurora.lens.shield.patches.*

object ShieldAssembler {
    private val patches: List<ShieldPatch> = listOf(
        NavPatch(), ScreenPatch(), CanvasPatch(), WebGLPatch(),
        AudioPatch(), RtcPatch(), PerfPatch(), TimezonePatch(), MiscPatch(),
    )

    fun build(profile: ShieldProfile = ShieldProfile.DEFAULT): String {
        val fragments = patches.joinToString("\n\n") {
            "// ── ${it.id} ──\n${it.generate(profile)}"
        }
        return """
(function() {
'use strict';
const DP = Object.defineProperty;
const FR = Object.freeze;
const seal = (o, p) => {
  for (const [k,v] of Object.entries(p))
    try { DP(o,k,{value:v,writable:false,enumerable:true,configurable:false}); }
    catch(_) {}
};

$fragments

console.log('[镜界] 指纹盾牌已激活');
})();
""".trimIndent()
    }
}
