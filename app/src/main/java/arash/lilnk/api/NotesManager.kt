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

const val noteUrl = "$BASE_URL/notes/notes.php"

fun createNote(title: String?, content: String, onResult: (Boolean, String, Int?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("user_id", Preferences[USER_ID, 0])
        put("action", "create")
        put("title", title)
        put("content", content)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, noteUrl, jsonRequest,
        { response ->
            Log.i("createNoteTAG", "create note response: $response")
            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.getString("short_url"), null)
            } else {
                val error = response.getString("error")
                val errorCode = response.getInt("code")
                onResult(false, error, errorCode)
                Log.i("createNoteTAG", "createNote: error code: $errorCode, error: $error")
            }
        },
        { error ->
            onResult(false, "Request error", 0)
            Log.i("createNoteTAG", "createNote: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun editNote(id: Int, title: String?, content: String, onResult: (Boolean, String?, Int?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("action", "edit")
        put("id", id)
        put("title", title)
        put("content", content)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, noteUrl, jsonRequest,
        { response ->
            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, response.getString("short_url"), null)
            } else {
                val errorCode = response.getInt("code")
                onResult(false, null, errorCode)
                Log.i("editNoteTAG", "editNote: error code: $errorCode")
            }
        },
        { error ->
            onResult(false, null, 0)
            Log.i("editNoteTAG", "editNote: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun deleteNote(id: Int, onResult: (Boolean, Int?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("action", "delete")
        put("id", id)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, noteUrl, jsonRequest,
        { response ->
            if (response.has("success") && response.getBoolean("success")) {
                onResult(true, null)
            } else {
                val errorCode = response.getInt("code")
                onResult(false, errorCode)
                Log.i("deleteNoteTAG", "deleteNote: ${response.getString("error")}")
            }
        },
        { error ->
            onResult(false, 0)
            Log.i("deleteNoteTAG", "deleteNote: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun displayNote(shortUrl: String, onResult: (Boolean, Notes?, Int?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("action", "display")
        put("short_url", shortUrl)
    }

    val request = JsonObjectRequest(
        Request.Method.POST, noteUrl, jsonRequest,
        { response ->
            if (response.has("content")) {
                val note = Notes(
                    id = response.getInt("id"),
                    title = response.getString("title"),
                    content = response.getString("content"),
                    shortUrl = response.getString("short_url"),
                    userId = response.getInt("user_id"),
                    createdAt = response.getString("created_at"),
                    updatedAt = response.optString("updated_at", null),
                    accessCount = response.getInt("access_count"),
                    lastAccessed = response.optString("last_accessed", null),
                )
                onResult(true, note, null)
            } else {
                val errorCode = response.getInt("code")
                onResult(false, null, errorCode)
                Log.i("displayNoteTAG", "displayNote: ${response.getString("error")}")
            }
        },
        { error ->
            onResult(false, null, 0)
            Log.i("displayNoteTAG", "displayNote: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}

fun getUserNotes(onResult: (Boolean, List<Notes>?, Int?) -> Unit) {

    val jsonRequest = JSONObject().apply {
        put("api_key", API_KEY)
        put("action", "list")
        put("user_id", Preferences[USER_ID, 0])
    }

    val request = JsonObjectRequest(
        Request.Method.POST, noteUrl, jsonRequest,
        { response ->
            Log.i("getUserNotesTAG", "notes response: $response")
            if (response.has("notes")) {
                val notesArray = response.getJSONArray("notes")
                val notes = mutableListOf<Notes>()
                for (i in 0 until notesArray.length()) {
                    val item = notesArray.getJSONObject(i)
                    val note = Notes(
                        id = item.getInt("id"),
                        title = item.getString("title"),
                        content = item.getString("content"),
                        shortUrl = item.getString("short_url"),
                        userId = item.getInt("user_id"),
                        createdAt = item.getString("created_at").convertToPersian(),
                        updatedAt = item.optString("updated_at"),
                        accessCount = item.getInt("access_count"),
                        lastAccessed = item.optString("last_accessed"),
                    )
                    notes.add(note)
                }
                onResult(true, notes, null)
            } else {
                onResult(false, null, 9006)
                Log.i("getUserNotesTAG", "getUserNotes: No notes found")
            }
        },
        { error ->
            onResult(false, null, 0)
            Log.i("getUserNotesTAG", "getUserNotes: $error")
        }
    )

    request.tag = Statics.REQUEST_TAG
    Lilnk.instance?.addToRequestQueue(request)
}
