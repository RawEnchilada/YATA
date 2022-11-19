package hu.bme.aut.android.todo.notifications


import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import hu.bme.aut.android.todo.items.Todo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import hu.bme.aut.android.todo.MainActivity


class Notification(
    private val id: Int,
    private val todo: Todo,
    private val ctx: Context
) {

    companion object{
        const val CHANNEL_ID = "TodoNotifications"
        const val GROUP = "Todos"
    }

    private val notificationManagerCompat: NotificationManagerCompat

    init {
        notificationManagerCompat = NotificationManagerCompat.from(ctx)
    }

    fun show(){
        createChannel()

        val onClearIntent = Intent(ctx, OnClearReceiver::class.java).apply {
            action = "NOTIFICATION_CLEARED"
            putExtra("todoId",todo.id)
        }
        val onClearPendingIntent = PendingIntent.getBroadcast(ctx,0,onClearIntent,PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE)

        val onClickIntent = Intent(ctx, MainActivity::class.java)
        val onClickPendingIntent = PendingIntent.getActivity(ctx,0,onClickIntent,PendingIntent.FLAG_IMMUTABLE)


        val notificationBuilder = NotificationCompat.Builder(ctx, CHANNEL_ID)

        notificationBuilder
            .setSmallIcon(androidx.appcompat.R.drawable.btn_checkbox_checked_mtrl)
            .setContentTitle("Todo")
            .setContentText(todo.description)
            .setAutoCancel(false)
            .setGroup(GROUP)
            .setDeleteIntent(onClearPendingIntent)
            .setContentIntent(onClickPendingIntent)
            .priority = NotificationCompat.PRIORITY_DEFAULT

        notificationManagerCompat.notify(id,notificationBuilder.build())
    }

    fun cancel(){
        notificationManagerCompat.cancel(id)
    }

    private fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name:CharSequence = "MyNotification"
            val description = "My notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID,name,importance)
            notificationChannel.description = description
            val notificationManager = ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}