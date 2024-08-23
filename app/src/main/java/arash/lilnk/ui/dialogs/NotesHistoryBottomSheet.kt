package arash.lilnk.ui.dialogs

import LoadingDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import arash.lilnk.api.getUserNotes
import arash.lilnk.model.Notes
import arash.lilnk.ui.components.LinksItem
import arash.lilnk.ui.components.NoteItem
import arash.lilnk.utilities.Lilnk
import arash.lilnk.utilities.Statics.REQUEST_TAG
import arash.lilnk.utilities.showSnackbar
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHistoryBottomSheet(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onEdit: (note: Notes) -> Unit,
    onDelete: (note: Notes) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val btmState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showHistory by rememberSaveable { mutableStateOf(false) }
    var noteDataList by remember { mutableStateOf<List<Notes>?>(null) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var shortUrl by rememberSaveable { mutableStateOf("") }

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
        onDismiss = {
            showResult = false
            showHistory = true
        },
    )

    // Launch the data fetching operation
    LaunchedEffect(Unit) {
        isLoading = true
        getUserNotes { success, notes, errorCode ->
            isLoading = false
            if (success && !notes.isNullOrEmpty()) {
                noteDataList = notes
                showHistory = true
            } else {
                showHistory = false
                val message = when (errorCode) {
                    0 -> "خطا در برقراری ارتباط با سرور!"
                    9006 -> "هیچ نوتی پیدا نشد! اولین نوت خود را بنویسید"
                    else -> "خطای ناشناخته"
                }
                showSnackbar(
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    message = message
                )
                onDismiss()
            }
        }
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = btmState,
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    noteDataList?.also { list ->
                        items(
                            count = list.size,
                        ) { index ->
                            NoteItem(
                                index = index,
                                note = list[index],
                                onNoteEdit = {
                                    onEdit(list[index])
                                    onDismiss()
                                },
                                onNoteDelete = { onDelete(list[index]) },
                            ) {
                                shortUrl = list[index].shortUrl
                                showHistory = false
                                showResult = true
                            }
                        }
                    }

                }
            }
        )
    }
}