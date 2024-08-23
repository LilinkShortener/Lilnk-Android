package arash.lilnk.ui.screens

import LoadingDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import arash.lilnk.api.createNote
import arash.lilnk.api.deleteNote
import arash.lilnk.api.editNote
import arash.lilnk.api.shortenLink
import arash.lilnk.ui.dialogs.NotesHistoryBottomSheet
import arash.lilnk.ui.dialogs.ResultBottomSheet
import arash.lilnk.ui.dialogs.ResultType
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.isValidCustomUrl
import arash.lilnk.utilities.isValidUrl
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    var noteTitle by rememberSaveable { mutableStateOf("") }
    var noteContent by rememberSaveable { mutableStateOf("") }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var shortUrl by rememberSaveable { mutableStateOf("") }

    var editing by rememberSaveable { mutableStateOf(false) }
    var noteId by rememberSaveable { mutableIntStateOf(0) }

    var showNoteHistory by rememberSaveable { mutableStateOf(false) }

    if (showNoteHistory) NotesHistoryBottomSheet(
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onDismiss = { showNoteHistory = false },
        onEdit = { note ->
            editing = true
            noteId = note.id
            noteTitle = note.title ?: ""
            noteContent = note.content
        },
        onDelete = { note ->
            showNoteHistory = false
            isLoading = true
            deleteNote(note.id) { success, errorCode ->
                isLoading = false
                if (success) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "نوت حذف شد و دیگر در دسترس کاربران نخواهد بود",
                    )
                } else {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = when (errorCode) {
                            0 -> "خطا در برقراری ارتباط با سرور!"
                            9003 -> "حذف نوت ممکن نبود!"
                            else -> "خطای ناشناخته"
                        }
                    )
                }
            }
        },
    )

    LoadingDialog(
        isLoading = isLoading,
        onDismiss = {
            Lilnk.instance?.cancelPendingRequest(REQUEST_TAG)
            isLoading = false
        }
    )

    ResultBottomSheet(
        resultType = ResultType.Note,
        shortenLink = shortUrl,
        hasAds = false,
        showResult = showResult,
        onDismiss = { showResult = false },
    )

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "از این ابزار می‌توانید برای ذخیره و اشتراک گذاری متن و یادداشت استفاده کنید.",
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = noteTitle,
            onValueChange = { noteTitle = it },
            label = { Text(text = "عنوان نوت") },
            singleLine = true,
            shape = CardDefaults.shape,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            label = { Text(text = "متن نوت") },
            shape = CardDefaults.shape,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4f)
        )

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = {
                if (noteContent.isBlank()) {
                    showSnackbar(
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = "متن یادداشت خود را وارد نمایید!"
                    )
                    return@Button
                }
                isLoading = true
                if (editing) {
                    editNote(
                        id = noteId,
                        title = noteTitle,
                        content = noteContent
                    ) { success, shortLink, errorCode ->
                        isLoading = false
                        if (success) {
                            editing = false
                            noteId = 0
                            noteTitle = ""
                            noteContent = ""
                            showSnackbar(
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = "ویرایش نوت انجام شد.",
                                actionLabel = "دریافت لینک",
                                action = {
                                    shortUrl = shortLink ?: ""
                                    showResult = true
                                }
                            )
                        } else {
                            showSnackbar(
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = when (errorCode) {
                                    0 -> "خطا در برقراری ارتباط با سرور!"
                                    9002 -> "بروزرسانی نوت ممکن نبود!"
                                    else -> "خطای ناشناخته"
                                }
                            )
                        }
                    }
                } else {
                    createNote(
                        title = noteTitle,
                        content = noteContent,
                    ) { success, result, errorCode ->
                        isLoading = false
                        if (success) {
                            shortUrl = result
                            showResult = true
                        } else {
                            showSnackbar(
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = when (errorCode) {
                                    0 -> "خطا در برقراری ارتباط با سرور!"
                                    9001 -> "مشکلی در ساخت نوت وجود دارد."
                                    else -> "خطای ناشناخته"
                                }
                            )
                        }
                    }
                }
            }, shape = CardDefaults.shape) {
                Text(text = if (editing) "ذخیره تغییرات" else "ذخیره و اشتراک‌گذاری")
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = {
                if (editing) {
                    editing = false
                    noteId = 0
                    noteTitle = ""
                    noteContent = ""
                } else
                    showNoteHistory = true

            }, shape = CardDefaults.shape) {
                Text(text = if (editing) "لغو" else "لیست نوت‌ها")
            }
        }
    }

}
