package com.aurora.lens.shield

interface ShieldPatch {
    val id: String
    fun generate(profile: ShieldProfile): String
}
