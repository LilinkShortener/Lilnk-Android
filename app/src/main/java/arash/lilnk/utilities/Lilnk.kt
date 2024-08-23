package arash.lilnk.utilities

import android.app.Application
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Lilnk : Application(){

    private val xTAG = "VolleyTAG"
    private var requestQueue: RequestQueue? = null
    private val lock = Any()

    companion object {
        var instance: Lilnk? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this@Lilnk
        Preferences.init(this@Lilnk)
    }

    private fun invoke(): RequestQueue = requestQueue ?: synchronized(lock) {
        requestQueue ?: Volley.newRequestQueue(applicationContext).also { requestQueue = it }
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String = xTAG) {
        req.tag = tag
        req.setShouldCache(false)
        req.setRetryPolicy(DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        invoke().add(req)
    }

    fun cancelPendingRequest(tag: String) = requestQueue?.cancelAll(tag)
}