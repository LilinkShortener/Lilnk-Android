package arash.lilnk.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arash.lilnk.R
import arash.lilnk.ui.theme.LilnkTheme
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlin.math.log10
import kotlin.math.max

@Composable
fun DashedLine(
    color: Color = MaterialTheme.colorScheme.onBackground,
    strokeWidth: Float = 2f,
    dashLength: Float = 10f,
    gapLength: Float = 10f,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(strokeWidth.dp)
            .padding(vertical = 0.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f),
            cap = StrokeCap.Butt
        )
    }
}

@Composable
fun LabeledSwitch(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
) {
    // Row composable containing a Text, spacing, and Switch
    Row(
        modifier = Modifier
            .noRippleClickable(
                role = Role.Checkbox,
                onClick = {
                    onStateChange(!state)
                }
            )
            .then(modifier), // Allows further modification of the Row
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        label()
        Spacer(modifier = Modifier.width(8.dp)) // Adds a spacing of 8.dp
        Switch(
            checked = state, // Current state of the switch
            onCheckedChange = null // No action when switch state changes
        )
    }
}

@Composable
fun IconText(painter: Painter, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}


@Composable
fun AnimatedCounter(
    targetValue: Int,
    baseDuration: Int = 300, // مدت زمان پایه
    maxDuration: Int = 2000, // حداکثر مدت زمان
) {
    val difference = max(1, targetValue)
    val duration = (log10(difference.toFloat()) * baseDuration).toInt().coerceAtMost(maxDuration)

    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = duration),
        label = "AnimatedCounter"
    )

    AutoResizeText(
        text = animatedValue.toString(),
        style = MaterialTheme.typography.displayMedium,
        maxLines = 1,
        fontSizeRange = FontSizeRange(
            min = MaterialTheme.typography.headlineSmall.fontSize,
            max = MaterialTheme.typography.displayMedium.fontSize,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CustomPreview() {
    LilnkTheme {
        IconText(
            painter = painterResource(id = R.drawable.ic_clicks),
            text = 100.toString(),
        )
    }
}
