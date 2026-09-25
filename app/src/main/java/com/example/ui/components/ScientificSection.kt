package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcAccentCyan
import com.example.ui.theme.CalcScientificBgDark

@Composable
fun ScientificSection(
    isExpanded: Boolean,
    hapticEnabled: Boolean,
    onScientificFunc: (String) -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sciBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val sciText = MaterialTheme.colorScheme.secondary

    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "sin",
                    onClick = { onScientificFunc("sin") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_sin"
                )
                CalcButton(
                    text = "cos",
                    onClick = { onScientificFunc("cos") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_cos"
                )
                CalcButton(
                    text = "tan",
                    onClick = { onScientificFunc("tan") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_tan"
                )
                CalcButton(
                    text = "ln",
                    onClick = { onScientificFunc("ln") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_ln"
                )
                CalcButton(
                    text = "log",
                    onClick = { onScientificFunc("log") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_log"
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "√",
                    onClick = { onScientificFunc("√") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 17.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_sqrt"
                )
                CalcButton(
                    text = "x²",
                    onClick = { onScientificFunc("x²") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_square"
                )
                CalcButton(
                    text = "xʸ",
                    onClick = { onScientificFunc("x^y") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_power"
                )
                CalcButton(
                    text = "π",
                    onClick = { onScientificFunc("π") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 16.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_pi"
                )
                CalcButton(
                    text = "e",
                    onClick = { onScientificFunc("e") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 16.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_e"
                )
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "1/x",
                    onClick = { onScientificFunc("1/x") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 14.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_inv"
                )
                CalcButton(
                    text = "x!",
                    onClick = { onScientificFunc("!") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = sciText,
                    fontSize = 15.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_fact"
                )
                CalcButton(
                    text = "MC",
                    onClick = onMemoryClear,
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_mc"
                )
                CalcButton(
                    text = "MR",
                    onClick = onMemoryRecall,
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_mr"
                )
                CalcButton(
                    text = "M+",
                    onClick = onMemoryAdd,
                    modifier = Modifier.weight(1f).height(44.dp),
                    backgroundColor = sciBg,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    hapticEnabled = hapticEnabled,
                    cornerRadius = 16.dp,
                    testTag = "btn_mplus"
                )
            }
        }
    }
}
