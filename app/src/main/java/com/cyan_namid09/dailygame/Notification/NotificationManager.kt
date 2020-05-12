package com.cyan_namid09.dailygame.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cyan_namid09.dailygame.MainActivity
import com.cyan_namid09.dailygame.R
import java.util.*

private const val NOTIFICATION_TODO_UPDATE_ID = "tweet-alert-notification"
private const val NOTIFICATION_TODO_UPDATE_REQUEST_CODE = 0

// チャンネル作成（Android8以上）
fun makeNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(NOTIFICATION_TODO_UPDATE_ID, "ツイート警告", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "ツイートされたことの確認"
            enableVibration(true)
            setShowBadge(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}

// 通知実行
fun notifyRuleUpdate(context: Context, title: String) {
    // PendingIntent作成
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_TODO_UPDATE_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT)

    // 通知のBuilderを作成（Android8以上か未満で、インスタンスが違う？）
    val builder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(context, NOTIFICATION_TODO_UPDATE_ID)
        else NotificationCompat.Builder(context)
    val notification = builder
        .setContentTitle("ルールを守れませんでした...")
        .setContentText(String.format("「%s」を守れなかったことをツイートします", title))
        .setContentIntent(pendingIntent)     // タップしたときに起動するインテント
        .setSmallIcon(R.drawable.ic_icon_for_notification)  // アイコン
        .setGroupSummary(false)
        .setAutoCancel(true)     // 通知をタップしたら、その通知を消す
        .build()

    val uuid = UUID.randomUUID().hashCode() // 通知のID。乱数にすれば同じ通知が重なっても、スタックされる。逆にIDが同じであれば同じ通知が残る
    NotificationManagerCompat.from(context).notify(uuid, notification)
}