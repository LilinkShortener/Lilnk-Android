package arash.lilnk.api

import android.util.Log
import arash.lilnk.model.Notes
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.API_KEY
import arash.lilnk.utilities.Statics.BASE_URL
import arash.lilnk.utilities.Statics.USER_ID
import arash.lilnk.utilities.convertToPersian
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

const val statsUrl = "$BASE_URL/stats.php"

fun getGeneralStats(onResult: (success: Boolean, totalUsers: Int, totalLinks: Int, totalClicks: Int, totalEarnings: Int) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("action", "general_stats")
    }

    val request = JsonObjectRequest(
        Request.Method.POST, statsUrl, jsonRequest,
        { response ->
            Log.i("getGeneralStats", "general stats response: $response")
            if (response.has("total_users")) {
                onResult(
                    true,
                    response.getInt("total_users"),
                    response.getInt("total_links"),
                    response.getInt("total_clicks"),
                    response.getInt("total_earnings"),
                )
            } else {
                onResult(false, 0, 0, 0, 0)
                Log.i("getGeneralStatsTAG", "getGeneralStats: No stats found")
            }
        },
        { error ->
            onResult(false, 0, 0, 0, 0)
            Log.i("getGeneralStatsTAG", "getGeneralStats: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}
