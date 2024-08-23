package arash.lilnk.utilities

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern


fun showSnackbar(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    onDismiss: () -> Unit = {},
    action: () -> Unit = {},
) {
    coroutineScope.launch {
        val snackResult = snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short,
            actionLabel = actionLabel
        )
        when (snackResult) {
            SnackbarResult.Dismissed -> onDismiss()
            SnackbarResult.ActionPerformed -> action()
        }
    }
}

// Function to validate URL
fun isValidUrl(url: String): Boolean {
    val urlPattern = Pattern.compile(
        "^(https?://)?([\\w.-]+)?[\\w-]+(\\.[a-z]{2,})+(:\\d+)?(/\\S*)?$",
        Pattern.CASE_INSENSITIVE
    )
    return urlPattern.matcher(url).matches()
}

// Function to validate custom URL
fun isValidCustomUrl(customUrl: String): Boolean {
    if (customUrl.isEmpty()) return true
    val customUrlPattern = Pattern.compile("^[a-zA-Z0-9]*$")
    return customUrlPattern.matcher(customUrl).matches()
}


fun copyToClipboard(context: Context, content: String) {
    val clipboard =
        ContextCompat.getSystemService(context, android.content.ClipboardManager::class.java)
    val clip = android.content.ClipData.newPlainText("Short URL", content)
    clipboard?.setPrimaryClip(clip)

    Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
}

fun shareText(context: Context, text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    startActivity(context, Intent.createChooser(intent, "Share via"), null)
}


fun String?.convertToPersian(ifNull: String = ""): String {
    if (this.isNullOrEmpty()) return ifNull
    try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = simpleDateFormat.parse(this)

        val persianDate = PersianDate(date)
        val persianDateFormat = PersianDateFormat("l، j F ماه Y")

        return persianDateFormat.format(persianDate)
    } catch (e: Exception) {
        e.printStackTrace()
        return this
    }
}