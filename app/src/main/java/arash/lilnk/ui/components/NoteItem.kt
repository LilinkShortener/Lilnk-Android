package arash.lilnk.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arash.lilnk.R
import arash.lilnk.model.Notes
import arash.lilnk.ui.theme.LilnkTheme
import arash.lilnk.utilities.Statics.DOMAIN
import arash.lilnk.utilities.convertToPersian

@Composable
fun NoteItem(
    index: Int,
    note: Notes,
    onNoteEdit: () -> Unit,
    onNoteDelete: () -> Unit,
    onNoteClick: () -> Unit
) {
    Column {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNoteClick() }
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = if (index > 0) 16.dp else 0.dp,
                    bottom = 16.dp
                )
        ) {
            note.title?.also { title ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily(Font(R.font.dana)),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ویرایش",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clickable(onClick = { onNoteEdit() })
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "حذف",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clickable(onClick = { onNoteDelete() })
                    )
                }
            }
            Text(
                text = note.content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Justify,
                fontFamily = FontFamily(Font(R.font.dana)),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.createdAt,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(.7f)
                )
                if (note.updatedAt != "null") {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edited!",
                        modifier = Modifier.size(16.dp)
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(16.dp)
                            .padding(start = 8.dp, end = 8.dp)
                    )
                }
                IconText(
                    painter = painterResource(id = R.drawable.ic_clicks),
                    text = note.accessCount.toString(),
                )
            }
        }

        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    LilnkTheme {
        NoteItem(
            index = 1,
            Notes(
                id = 12,
                userId = 12345,
                title = "عنوان",
                content = """لورم ایپسوم متن ساختگی با تولید سادگی نامفهوم از صنعت چاپ، و با استفاده از طراحان گرافیک است، چاپگرها و متون بلکه روزنامه و مجله در ستون و سطرآنچنان که لازم است، و برای شرایط فعلی تکنولوژی مورد نیاز، و کاربردهای متنوع با هدف بهبود ابزارهای کاربردی می باشد، کتابهای زیادی در شصت و سه درصد گذشته حال و آینده، شناخت فراوان جامعه و متخصصان را می طلبد، تا با نرم افزارها شناخت بیشتری را برای طراحان رایانه ای علی الخصوص طراحان خلاقی، و فرهنگ پیشرو در زبان فارسی ایجاد کرد، در این صورت می توان امید داشت که تمام و دشواری موجود در ارائه راهکارها، و شرایط سخت تایپ به پایان رسد و زمان مورد نیاز شامل حروفچینی دستاوردهای اصلی، و جوابگوی سوالات پیوسته اهل دنیای موجود طراحی اساسا مورد استفاده قرار گیرد.""",
                shortUrl = "$DOMAIN/abcd1234",
                createdAt = "2024-08-01 12:00:00".convertToPersian(),
                updatedAt = "2024-08-03 12:00:00",
                accessCount = 100,
                lastAccessed = "2024-08-01 12:00:00".convertToPersian(),
            ),
            onNoteDelete = {},
            onNoteEdit = {}
        ) {

        }
    }
}