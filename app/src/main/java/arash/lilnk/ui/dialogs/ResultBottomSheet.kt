package arash.lilnk.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arash.lilnk.utilities.Statics.DOMAIN
import arash.lilnk.utilities.copyToClipboard
import arash.lilnk.utilities.shareText
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

enum class ResultType { Link, Note }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    resultType: ResultType,
    showResult: Boolean,
    shortenLink: String,
    hasAds: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val btmState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shortUrl by rememberUpdatedState(newValue = if (resultType == ResultType.Link) "$DOMAIN//$shortenLink" else "$DOMAIN//notes/$shortenLink")

    if (showResult) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = btmState,
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = if (resultType == ResultType.Link) "لینک کوتاه شده:" else "لینک نوت:",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                    OutlinedTextField(
                        value = shortUrl,
                        onValueChange = { },
                        textStyle = TextStyle(textDirection = TextDirection.Ltr),
                        readOnly = true,
                        singleLine = true,
                        shape = CardDefaults.shape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(false) {}
                            .clickable(false) { }
                            .focusable(false)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "کیو‌آر کد",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "برای ذخیره بارکد اینجا کلیک کنید.",
                                    fontSize = 13.sp,
                                    modifier = Modifier.alpha(.7f)
                                )
                            }
                            Image(
                                painter = rememberQrCodePainter(shortUrl),
                                contentDescription = "QR code for your link.",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .padding(4.dp)
                            )
                        }
                    }

                    if (hasAds)
                        Text(
                            text = "درآمد حاصل از این لینک و آمار کلیک آن را می‌توانید از قسمت لینک‌ها مشاهده کنید.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .alpha(.7f)
                                .padding(4.dp)
                        )

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                onDismiss()
                                copyToClipboard(context = context, content = shortUrl)
                            }
                        ) {
                            Text(text = "کپی")
                        }

                        TextButton(
                            onClick = {
                                onDismiss()
                                shareText(context = context, text = shortUrl)
                            }
                        ) {
                            Text(text = "اشتراک‌گذاری")
                        }
                    }

                }
            }
        )
    }
}