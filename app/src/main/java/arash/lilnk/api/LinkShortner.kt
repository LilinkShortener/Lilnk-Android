package arash.lilnk.api

import android.util.Log
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.API_KEY
import arash.lilnk.utilities.Statics.BASE_URL
import arash.lilnk.utilities.Statics.USER_ID
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


const val shorten = "$BASE_URL/shorten.php"

fun shortenLink(
    originalLink: String,
    customLink: String,
    withAds: Boolean,
    onResult: (Boolean, String, Int?) -> Unit
) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("user_id", Preferences[USER_ID, 0])
        put("original_link", originalLink)
        put("short_link", customLink)
        put("with_ads", withAds)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, shorten, jsonRequest,
        { response ->
            Log.i("shortenLinkTAG", response.toString())

            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.optString("short_link"), null)
            } else {
                val error = response.optString("error")
                val errorCode = response.optInt("code", 0)
                Log.i("shortenLinkTAG", "Error: $error with code $errorCode")
                onResult(false, error, errorCode)
            }
        }, { error ->
            onResult(false, "Request error", 0)
            Log.e("shortenLinkTAG", "ShortenError: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}
