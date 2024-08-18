import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun LoadingDialog(
    isLoading: Boolean,
    isDismissable: Boolean = true,
    onDismiss: () -> Unit
) {
    if (isLoading) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceBright
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )
                        Column {
                            Text(
                                text = "لطفا صبر کنید...",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(
                                    top = 18.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                )
                            )
                            Text(
                                text = "در حال دریافت اطلاعات از سرور",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(
                                    bottom = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ).alpha(.7f)
                            )
                        }
                    }


                    if (isDismissable) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            TextButton(
                                onClick = {onDismiss()},
                            ) {
                                Text(text = "انصراف")
                            }
                        }
                    }

                }
            }
        }
    }
}
