package arash.lilnk.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

fun Modifier.noRippleClickable(role: Role = Role.Button, onClick: () -> Unit): Modifier = composed {
    this.clickable(
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}