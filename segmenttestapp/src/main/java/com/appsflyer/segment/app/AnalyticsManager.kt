import android.content.Context
import com.segment.analytics.kotlin.core.Analytics

object AnalyticsManager {
    lateinit var analytics: Analytics
    private const val SEGMENT_WRITE_KEY: String = "p3uCyX72FjaikfQVyxvUGSzBpRst2flg"

    fun initialize(context: Context) {
        analytics = com.segment.analytics.kotlin.android.Analytics(SEGMENT_WRITE_KEY, context) {
            flushAt = 3
            trackApplicationLifecycleEvents = true
        }
    }
}