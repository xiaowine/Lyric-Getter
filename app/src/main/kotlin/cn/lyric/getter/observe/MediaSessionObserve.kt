package cn.lyric.getter.observe

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import cn.xiaowine.xkt.LogTool.log

open class MediaSessionObserve(context: Context) {
    private var mediaSessionManager: MediaSessionManager? = null
    private val activeControllers = mutableMapOf<MediaController, MediaControllerCallback>()

    // 监听活跃会话的变化
    private val activeSessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        "activeSessionsListener: ${controllers?.size}".log()
        if (controllers?.size == 0)
            onCleared()

        // 清理之前的回调
        activeControllers.forEach { it.key.unregisterCallback(it.value) }
        activeControllers.clear()

        controllers?.let {
            it.forEach { controller ->
                registerMediaControllerCallback(controller)
            }
        }
    }

    init {
        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
        mediaSessionManager?.getActiveSessions(ComponentName(context, NotificationListenerService::class.java))?.forEach {
            registerMediaControllerCallback(it)
        }
        mediaSessionManager?.addOnActiveSessionsChangedListener(
            activeSessionsListener,
            ComponentName(context, NotificationListenerService::class.java)
        )
    }

    private fun registerMediaControllerCallback(controller: MediaController) {
        // handleMediaController(controller)

        val callback = MediaControllerCallback(controller)
        activeControllers[controller] = callback
        controller.registerCallback(callback)
    }

    private fun handleMediaController(controller: MediaController) {
        controller.metadata?.let { metadata ->
            onMediaDataChanged(controller.packageName, metadata)
        }

        controller.playbackState?.let { state ->
            onStateChanged(controller.packageName, state.state)
        }
    }

    // MediaController 回调
    private inner class MediaControllerCallback(private val controller: MediaController) :
        MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            metadata?.let {
                onMediaDataChanged(controller.packageName, it)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            state?.let {
                onStateChanged(controller.packageName, it.state)
            }
        }
    }

    // 显示媒体元数据
    // private fun displayMediaMetadata(packageName: String, metadata: MediaMetadata) {
    //     val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
    //     val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
    //     if (this.artist != artist) {
    //         this.artist = artist
    //         onMediaDataChanged(packageName, title)
    //     }
    // }

    fun cleanup() {
        mediaSessionManager?.removeOnActiveSessionsChangedListener(activeSessionsListener)
        activeControllers.forEach { it.key.unregisterCallback(it.value) }
    }

    open fun onMediaDataChanged(packageName: String, metadata: MediaMetadata) {}

    open fun onStateChanged(packageName: String, state: Int) {}

    open fun onCleared() {}
}
