package arash.lilnk.api

import arash.lilnk.utilities.Preferences
import arash.lilnk.utilities.Statics.API_KEY
import arash.lilnk.utilities.Statics.BASE_URL
import arash.lilnk.utilities.Statics.USER_ID
import com.android.volley.toolbox.JsonObjectRequest
import android.util.Log
import arash.lilnk.model.Withdrawal
import arash.lilnk.model.WithdrawalsStats
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.convertToPersian
import com.android.volley.Request
import org.json.JSONObject


const val withdrawAction = "$BASE_URL/withdraw.php"
const val withdrawalsStatsUrl = "$BASE_URL/stats.php"

fun processWithdrawal(
    iban: String,
    name: String,
    surname: String,
    onResult: (Boolean, String, Int?) -> Unit
) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("user_id", Preferences[USER_ID, 0])
        put("iban", iban)
        put("name", name)
        put("surname", surname)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, withdrawAction, jsonRequest,
        { response ->
            Log.i("processWithdrawalTAG", response.toString())

            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.optString("message"), null)
            } else {
                val error = response.optString("error")
                val errorCode = response.optInt("code", 0)
                Log.i("processWithdrawalTAG", "Error: $error with code $errorCode")
                onResult(false, error, errorCode)
            }
        }, { error ->
            onResult(false, "Request error", 0)
            Log.e("processWithdrawalTAG", "WithdrawalError: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun getWithdrawalsStats(onResult: (Boolean, String?, WithdrawalsStats?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("user_id", Preferences[USER_ID, 0])
        put("action", "withdrawals_stats")
    }

    val request = JsonObjectRequest(
        Request.Method.POST, withdrawalsStatsUrl, jsonRequest,
        { response ->
            Log.i("WithdrawalsStatsTAG", response.toString())

            if (response.has("user_id")) {
                val withdrawalsList = mutableListOf<Withdrawal>()
                val withdrawalsArray = response.optJSONArray("withdrawals")

                withdrawalsArray?.let {
                    for (i in 0 until it.length()) {
                        val withdrawalJson = it.getJSONObject(i)
                        val withdrawal = Withdrawal(
                            id = withdrawalJson.optInt("id"),
                            userId = withdrawalJson.optInt("user_id"),
                            iban = withdrawalJson.optString("iban"),
                            amount = withdrawalJson.optInt("amount"),
                            name = withdrawalJson.optString("name"),
                            surname = withdrawalJson.optString("surname"),
                            requestTime = withdrawalJson.optString("request_time").convertToPersian(),
                            status = withdrawalJson.optInt("status")
                        )
                        withdrawalsList.add(withdrawal)
                    }
                }

                val withdrawalsStats = WithdrawalsStats(
                    userId = response.optInt("user_id"),
                    email = response.optString("email"),
                    registrationTime = response.optString("registration_time"),
                    totalLinks = response.optInt("total_links"),
                    totalClicks = response.optInt("total_clicks"),
                    adsLinks = response.optInt("ads_links"),
                    noAdsLinks = response.optInt("no_ads_links"),
                    adsEarnings = response.optInt("ads_earnings"),
                    currentBalance = response.optInt("current_balance"),
                    withdrawals = withdrawalsList
                )

                onResult(true, null, withdrawalsStats)
            } else {
                val error = response.optString("error")
                onResult(false, error, null)
            }
        }, { error ->
            onResult(false, error.toString(), null)
            Log.e("WithdrawalsStatsTAG", "WithdrawalsError: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

