package com.aurora.lens.ui

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aurora.lens.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<RadioGroup>(R.id.rg_profile).setOnCheckedChangeListener { _, id ->
            val name = when (id) {
                R.id.rb_desktop -> "desktop"
                R.id.rb_mobile -> "mobile"
                else -> "tablet"
            }
            getSharedPreferences("shield", MODE_PRIVATE)
                .edit()
                .putString("profile_name", name)
                .apply()
            Toast.makeText(this, "配置已保存，重启标签页生效", Toast.LENGTH_SHORT).show()
        }
    }
}
