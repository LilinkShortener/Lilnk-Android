package arash.lilnk.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import arash.lilnk.model.Withdrawal
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.R

@Composable
fun WithdrawalItem(item: Withdrawal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "برداشت ${item.requestTime}",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "مقدار ${item.amount} تومان به نام ${item.name} ${item.surname}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "به آدرس ${item.iban}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(36.dp)
                    .padding(vertical = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "وضعیت: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_indicator),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colorResource(id = if (item.status == 0) R.color.yellow else R.color.green)
                )
                Text(
                    text = if (item.status == 0) "در انتظار" else "تایید شده",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = if (item.status == 0) R.color.yellow else R.color.green),
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(
            modifier = Modifier.alpha(.6f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "#${item.id}",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "شناسه",
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawalItemPreview() {
    LilnkTheme {
        WithdrawalItem(
            Withdrawal(
                id = 12,
                userId = 12345,
                iban = "IR123456789012345678901234",
                amount = 1000,
                name = "آرش",
                surname = "عزیزی",
                requestTime = "2023-08-01 12:00:00",
                status = 0
            )
        )
    }
}