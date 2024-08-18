package arash.lilnk.api

import android.util.Log
import android.util.Patterns
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics
import arash.lilnk.utilities.Statics.API_KEY
import arash.lilnk.utilities.Statics.BASE_URL
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


const val login = "$BASE_URL/login.php"
const val register = "$BASE_URL/register.php"

fun loginUser(email: String, password: String, onResult: (Boolean, Int) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("email", email)
        put("password", password)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, login, jsonRequest,
        { response ->
            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.getInt("id"))
            } else {
                onResult(false, response.getInt("code"))
                Log.i("loginTAG", "loginUser: ${response.getString("error")}")
            }
        },
        { error ->
            onResult(false, 0)
            Log.i("loginTAG", "loginUser: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun registerUser(email: String, password: String, onResult: (Boolean, Int) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("email", email)
        put("password", password)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, register, jsonRequest,
        { response ->
            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.getInt("id"))
            } else {
                onResult(false, response.getInt("code"))
                Log.i("registerTAG", "registerUser: ${response.getString("error")}")
            }
        },
        { error ->
            onResult(false, 0)
            Log.i("registerTAG", "registerUser: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun isValidEmail(email: String): Boolean {
    return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    val hasUpperLowerCase = password.any { it.isUpperCase() } || password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val isValidLength = password.length >= 8
    return hasUpperLowerCase && hasDigit && isValidLength
}
