package cn.lyric.getter.observe

import android.app.ActivityManagerHidden
import android.content.pm.IPackageManager
import android.os.Build
import android.os.ServiceManager
import cn.xiaowine.xkt.LogTool.log
import rikka.hidden.compat.ActivityManagerApis
import rikka.hidden.compat.adapter.UidObserverAdapter

class UidObserveService(private val onUidGoneCallback: (String) -> Unit) {

    private var appUid = 0

    private var appPackageName = ""

    private val uidObserver = object : UidObserverAdapter() {
        override fun onUidGone(uid: Int, disabled: Boolean) {
            if (uid != appUid) return
            onUidGoneCallback.invoke(appPackageName)
            ActivityManagerApis.unregisterUidObserver(this)
            appUid = 0
            appPackageName = ""
        }
    }

    fun registerForPackage(packageName: String) {
        if (appUid != 0 && appPackageName != "") return
        appPackageName = packageName
        getIPackageManager().onSuccess { pm ->
            appUid = getPackageUidCompat(pm, packageName, 0, 0)
            ActivityManagerApis.registerUidObserver(
                uidObserver,
                ActivityManagerHidden.UID_OBSERVER_GONE,
                ActivityManagerHidden.PROCESS_STATE_UNKNOWN,
                null
            )
            "package: $packageName, uid: $appUid registered".log()
        }
    }

    private fun getIPackageManager(): Result<IPackageManager> {
        try {
            val binder = ServiceManager.getService("package")
            return Result.success(IPackageManager.Stub.asInterface(binder))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun getPackageUidCompat(pms: IPackageManager, packageName: String, flags: Long, userId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pms.getPackageUid(packageName, flags, userId)
        } else {
            pms.getPackageUid(packageName, flags.toInt(), userId)
        }
    }

}