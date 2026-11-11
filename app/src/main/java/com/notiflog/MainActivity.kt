package com.notiflog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply {
            textSize = 16f
            setPadding(24, 48, 24, 24)
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 32, 16, 16)
        }

        box.addView(status)

        box.addView(Button(this).apply {
            text = "১. Notification access চালু করো"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        })

        box.addView(Button(this).apply {
            text = "২. টেস্ট মেসেজ পাঠাও"
            setOnClickListener {
                Sender.send("✅ টেস্ট — NotifLog থেকে পাঠানো হচ্ছে।")
                status.text = "টেস্ট পাঠানো হয়েছে। Telegram দেখো।"
            }
        })

        box.addView(Button(this).apply {
            text = "৩. স্ট্যাটাস রিফ্রেশ"
            setOnClickListener { refresh() }
        })

        setContentView(ScrollView(this).apply { addView(box) })
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val flat = Settings.Secure.getString(
            contentResolver, "enabled_notification_listeners"
        ).orEmpty()
        status.text = if (flat.contains(packageName)) {
            "✅ অনুমতি আছে। এখন ফোনটা ছেড়ে দাও — নতুন সব মেসেজ Telegram-এ যাবে।"
        } else {
            "⚠️ অনুমতি নেই। উপরের ১ নম্বর বাটনে চাপো, তালিকায় NotifLog খুঁজে চালু করো, তারপর ফিরে এসে ২ চাপো।"
        }
    }
}
