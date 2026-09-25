package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    cornerRadius: Dp = 24.dp,
    testTag: String = "btn_$text",
    hapticEnabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .testTag(testTag)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        tonalElevation = 2.dp,
        shadowElevation = if (isPressed) 0.dp else 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .then(
                    if (onLongClick != null) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    if (hapticEnabled) {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    }
                                },
                                onTap = { onClick() },
                                onLongPress = {
                                    if (hapticEnabled) {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    }
                                    onLongClick()
                                }
                            )
                        }
                    } else {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = androidx.compose.material3.ripple()
                        ) {
                            if (hapticEnabled) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            }
                            onClick()
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
