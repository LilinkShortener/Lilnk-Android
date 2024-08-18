package arash.lilnk.api

import android.util.Log
import arash.lilnk.model.Links
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.API_KEY
import arash.lilnk.utilities.Statics.BASE_URL
import arash.lilnk.utilities.Statics.USER_ID
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

// URL for the user stats API endpoint
const val userStatsUrl = "$BASE_URL/stats.php"

// Function to fetch user link statistics
fun getUserLinkStats(
    onResult: (Boolean, Int?, List<Links>?, Int?) -> Unit
) {
    // Create the JSON request object
    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("user_id", Preferences[USER_ID, 0])
        put("action", "link_stats")
    }

    // Create the POST request
    val request = JsonObjectRequest(
        Request.Method.POST, userStatsUrl, jsonRequest,
        { response ->
            Log.i("getUserLinkStatsTAG", response.toString())

            // Parse the response if successful
            if (response.has("user_id")) {
                val linksArray = response.getJSONArray("links")
                val linkDataList = mutableListOf<Links>()

                for (i in 0 until linksArray.length()) {
                    val linkObject = linksArray.getJSONObject(i)
                    val linkData = Links(
                        shortUrl = linkObject.getString("short_url"),
                        originalUrl = linkObject.getString("original_url"),
                        createdAt = linkObject.getString("created_at"),
                        accessCount = linkObject.getInt("access_count"),
                        earnings = linkObject.getInt("earnings")
                    )
                    linkDataList.add(linkData)
                }

                val totalEarnings = response.getInt("total_earnings")
                onResult(true, null, linkDataList, totalEarnings)
            } else {
                // Handle the error response
                val error = response.optString("error")
                val errorCode = response.optInt("code")
                Log.i("getUserLinkStatsTAG", "Error: $error with code $errorCode")
                onResult(false, errorCode, null, null)
            }
        },
        { error ->
            // Handle request error
            Log.e("getUserLinkStatsTAG", "Request error: $error")
            onResult(false, 0, null, null)
        }
    )

    // Add the request to the request queue
    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}
