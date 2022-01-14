package app.tummo.test_flickering

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.Toast
import java.util.*
import android.content.pm.ApplicationInfo
import android.util.Log
import java.lang.Exception
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class AppblockService : Service() {

    companion object {
        private var instance: AppblockService? = null

        fun  isInstanceCreated(): Boolean {
            return instance != null
        }
    }

    //met


    private val timer: Timer? = Timer()

     val UPDATE_INTERVAL = (2000).toLong()
     val DELAY_INTERVAL: Long = 0


    override fun onCreate() {
        instance = this
        timer?.scheduleAtFixedRate (
            object : TimerTask() {
                override fun run() {
                    val foregroundApp = getForegroundApp()
                    killApp(foregroundApp)
                }
            },
            DELAY_INTERVAL,
            UPDATE_INTERVAL
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()


        val chan = NotificationChannel(
            "1",
            "App Block Service",
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, "1" )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("App Blocker Running")
            .setContentText("")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)

        toast(this, "App Blocker Service Started")

        return START_NOT_STICKY
    }

    fun killApp(packageToKill:String) {
        try {
            var packageInfo = packageManager.getApplicationInfo(packageToKill, 0)


            val myPackage = applicationContext.packageName

            if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                return
            }
            if (packageInfo.packageName == myPackage) {
                return
            }
            if (packageInfo.packageName == packageToKill) {
                toast(this,"Closing ${packageManager.getApplicationLabel(packageInfo)}")
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(startMain)
            }
        } catch (exception: Exception) {
            Log.d("Error", exception.toString())
        }
    }

    fun toast(context: Context?, text: String?) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(context, text, Toast.LENGTH_LONG).show() }
    }

    fun getForegroundApp(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var foregroundApp: String? = null

            val time = System.currentTimeMillis()

            val mUsageStatsManager =
                (getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager)

            val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time)
            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {

                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    foregroundApp = event.packageName
                }
            }
            return foregroundApp.toString()
        }
        return ""
    }




    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        instance = null
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        timer?.cancel();
        super.onDestroy();
    }
}