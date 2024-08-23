package arash.lilnk.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arash.lilnk.R
import arash.lilnk.model.Links
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.utilities.Statics.DOMAIN

@Composable
fun LinksItem(item: Links, onItemClick: () -> Unit) {
    Column {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(12.dp)
        ) {
            Text(
                text = "$DOMAIN/${item.shortUrl}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(textDirection = TextDirection.Ltr),
                fontFamily = FontFamily(Font(R.font.dana))
            )
            Text(
                text = item.originalUrl,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary,
                style = TextStyle(textDirection = TextDirection.Ltr),
                fontFamily = FontFamily(Font(R.font.dana))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = item.createdAt, fontSize = 14.sp, modifier = Modifier.weight(1f))
                IconText(
                    painter = painterResource(id = R.drawable.ic_earnings),
                    text = item.earnings.toString(),
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(start = 8.dp, end = 8.dp)
                )
                IconText(
                    painter = painterResource(id = R.drawable.ic_clicks),
                    text = item.accessCount.toString(),
                )
            }
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun LinksItemPreview() {
    LilnkTheme {
        LinksItem(
            Links(
                shortUrl = "$DOMAIN/abcd1234",
                originalUrl = "https://www.bing.com/search?q=json+to+android+data+class&cvid=f92e83276d94467d9fc563bf1d5f3903&gs_lcrp=EgZjaHJvbWUyBggAEEUYOdIBCDYyMjlqMGo0qAIIsAIB&FORM=ANAB01&PC=U531",
                createdAt = "2024-08-01 12:00:00",
                accessCount = 100,
                earnings = 50,
            )
        ) {

        }
    }
}