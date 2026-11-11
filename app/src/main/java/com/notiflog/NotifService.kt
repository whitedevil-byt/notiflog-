package com.notiflog

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifService : NotificationListenerService() {

    private val seen = LinkedHashMap<String, Long>()
    private var greeted = false

    override fun onListenerConnected() {
        super.onListenerConnected()
        if (!greeted) {
            greeted = true
            Sender.send("🟢 NotifLog সংযুক্ত হয়েছে। এখন থেকে মেসেজ আসতে থাকবে।")
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val n = sbn.notification ?: return
        if (sbn.packageName == packageName) return
        if (n.flags and Notification.FLAG_ONGOING_EVENT != 0) return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val ex = n.extras
        val title = ex.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = (ex.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?: ex.getCharSequence(Notification.EXTRA_TEXT))
            ?.toString()?.trim().orEmpty()

        if (title.isEmpty() && text.isEmpty()) return
        if (text == "Checking for new messages") return

        val key = sbn.packageName + "|" + title + "|" + text + "|" + sbn.postTime
        synchronized(seen) {
            if (seen.containsKey(key)) return
            seen[key] = System.currentTimeMillis()
            if (seen.size > 400) {
                val it = seen.keys.iterator()
                if (it.hasNext()) { it.next(); it.remove() }
            }
        }

        val time = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
        Sender.send("📩 ${appLabel(sbn.packageName)}\n🕒 $time\n\n$title\n$text")
    }

    private fun appLabel(pkg: String): String = try {
        val pm = packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    } catch (_: Exception) {
        pkg
    }
}
