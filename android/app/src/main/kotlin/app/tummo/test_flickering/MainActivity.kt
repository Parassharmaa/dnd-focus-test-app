package app.tummo.test_flickering

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import android.telephony.TelephonyManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


private const val CHANNEL = "app.tummo.test_flickering"

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "grantNotificationPolicyAccess" -> {
                    grantNotificationPolicyAccess()
                }
                "setInteruptionFilter" -> {
                    var filter: Int = call.argument<Int>("filter") as Int
                    setInteruptionFilter(filter)
                }
                "getInteruptionFilter" -> {
                    val filter = getInteruptionFilter()
                    result.success(filter)
                }
                "isSimCardPresent" -> {
                    val filter = isSimCardPresent()
                    result.success(filter)
                }
                "requestOverlayPermission" -> {
                    requestOverlayPermission()
                }
                "startAppblockService" -> {
                    startAppblockService()
                }
                "stopAppblockService" -> {
                    stopAppblockService()
                }
                "isAppblockServiceRunning" -> {
                   val res =  isAppblockServiceRunning();
                    result.success(res)
                }
                "grantUsagePermission" -> {
                    grantUsagePermission()
                }
                "isNotificationPermission" -> {
                    isNotificationPermission()
                }
                "grantNotificationAccess" -> {
                    grantNotificationAccess()
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun grantNotificationPolicyAccess() {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", applicationInfo.uid);
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);

            context.startActivity(intent)
    }


private fun isNotificationPermission(): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    val packageName = this.packageName
    return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
}

private fun grantNotificationAccess() {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent);
}

    private fun setInteruptionFilter(value: Int) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNotificationManager.setInterruptionFilter(value)
        }
    }

    private fun getInteruptionFilter(): Int {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mNotificationManager.currentInterruptionFilter
        }
        return -1;
    }

    private fun isSimCardPresent(): Boolean {
        val mTelephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return mTelephonyManager.simState == TelephonyManager.SIM_STATE_READY
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        )


        startActivity(intent)
    }

    private fun grantUsagePermission() {
        val intent = Intent(
            Settings.ACTION_USAGE_ACCESS_SETTINGS
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data =  Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun startAppblockService() {
        Intent(this, AppblockService::class.java).also { intent ->
            ContextCompat.startForegroundService(this, intent)
        }
    }

    private fun stopAppblockService() {
        Intent(this, AppblockService::class.java).also { intent ->
            stopService(intent)
        }
    }

    private fun isAppblockServiceRunning(): Boolean {
        return AppblockService.isInstanceCreated()
    }
}
