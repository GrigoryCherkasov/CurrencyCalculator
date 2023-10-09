package ws.grigory.currencycalculator

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val LATENCY = 3000L
class CalculatorApplication : Application() {
    private val threadLock = Mutex()
    private val dataLock = Mutex()
    private var lastTime = 0L
    private val scope = CoroutineScope(Dispatchers.IO)
    companion object {
        val INVALIDATE_INTENT =
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(
                Constants.INVALIDATE,
                true
            )
    }
    fun launchEvalTimeout() {
        scope.launch {
            dataLock.withLock {
                if (lastTime == 0L) {
                    lastTime = System.currentTimeMillis()

                    launch {
                        while (true) {
                            var timeout: Long

                            dataLock.withLock {
                                timeout = LATENCY - (System.currentTimeMillis() - lastTime)
                            }

                            if (timeout > 0) {
                                delay(timeout)
                            } else {
                                sendBroadcast(INVALIDATE_INTENT)
                                threadLock.lock()
                            }
                        }
                    }
                } else {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTime >= LATENCY) {
                        threadLock.unlock()
                    }
                    lastTime = currentTime
                }
            }
        }
    }
}