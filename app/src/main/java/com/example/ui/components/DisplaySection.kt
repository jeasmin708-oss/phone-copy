package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState
import com.example.ui.theme.CalcAccentAmber
import com.example.ui.theme.CalcErrorRed

@Composable
fun DisplaySection(
    uiState: CalculatorUiState,
    onAngleToggle: () -> Unit,
    onHistoryClick: () -> Unit,
    onHapticToggle: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Auto-scroll expression to end when it changes
    LaunchedEffect(uiState.expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Toolbar: Status badges, History, Haptics, Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Angle Mode Badge (DEG / RAD)
                AssistChip(
                    onClick = onAngleToggle,
                    label = {
                        Text(
                            text = if (uiState.isDegreeMode) "DEG" else "RAD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("angle_mode_chip"),
                    shape = RoundedCornerShape(8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    border = null
                )

                // Last evaluation tape
                if (!uiState.lastEvaluatedExpression.isNullOrEmpty() && uiState.finalResult == null) {
                    Text(
                        text = "Ans: ${uiState.lastEvaluatedExpression}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Action buttons: History, Haptics, Copy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onHapticToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_haptics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Toggle vibration",
                            tint = if (uiState.hapticEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("open_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Calculation history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val textToCopy = uiState.finalResult ?: uiState.previewResult ?: uiState.expression
                            if (textToCopy.isNotBlank()) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Calculator Result", textToCopy.replace(",", ""))
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied: $textToCopy", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("copy_result_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Expression Area (with horizontal scroll for long equations)
            val displayText = uiState.expression.ifEmpty { "0" }
            val fontSize = when {
                displayText.length > 20 -> 24.sp
                displayText.length > 12 -> 32.sp
                displayText.length > 7 -> 40.sp
                else -> 48.sp
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    color = if (uiState.expression.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("calculator_expression_text")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Preview or Final Result row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Backspace icon button on the bottom left of display for easy thumb reach
                AnimatedVisibility(
                    visible = uiState.expression.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CalcButton(
                        text = "⌫",
                        onClick = onBackspace,
                        onLongClick = onClear,
                        modifier = Modifier.size(44.dp, 36.dp),
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cornerRadius = 12.dp,
                        testTag = "backspace_icon_button",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace (Hold to clear all)",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }

                if (uiState.expression.isEmpty()) {
                    Spacer(modifier = Modifier.width(44.dp))
                }

                // Result or Preview
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    when {
                        uiState.isError -> {
                            Text(
                                text = uiState.errorMessage ?: "Error",
                                color = CalcErrorRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.testTag("calculator_error_text")
                            )
                        }
                        uiState.finalResult != null -> {
                            Text(
                                text = "= ${uiState.finalResult}",
                                color = CalcAccentAmber,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("calculator_final_result_text")
                            )
                        }
                        uiState.previewResult != null -> {
                            Text(
                                text = "= ${uiState.previewResult}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("calculator_preview_result_text")
                            )
                        }
                    }
                }
            }
        }
    }
}
