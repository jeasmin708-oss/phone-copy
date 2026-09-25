package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcAccentAmber
import com.example.ui.theme.CalcDigitBgDark
import com.example.ui.theme.CalcDigitTextDark
import com.example.ui.theme.CalcEqualsBgDark
import com.example.ui.theme.CalcEqualsTextDark
import com.example.ui.theme.CalcErrorRed
import com.example.ui.theme.CalcFunctionBgDark
import com.example.ui.theme.CalcFunctionTextDark
import com.example.ui.theme.CalcOperatorBgDark
import com.example.ui.theme.CalcOperatorTextDark

@Composable
fun KeypadSection(
    isScientificExpanded: Boolean,
    hapticEnabled: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onParenthesis: () -> Unit,
    onPercent: () -> Unit,
    onToggleSign: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    onToggleScientific: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 64.dp
    val spacing = 10.dp

    // Color definitions
    val digitBg = MaterialTheme.colorScheme.surfaceVariant
    val digitText = MaterialTheme.colorScheme.onSurface
    val funcBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val funcText = MaterialTheme.colorScheme.onSurfaceVariant
    val opBg = MaterialTheme.colorScheme.primary
    val opText = MaterialTheme.colorScheme.onPrimary
    val equalsBg = MaterialTheme.colorScheme.primary
    val equalsText = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Advanced / Scientific Expand bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = isScientificExpanded,
                onClick = onToggleScientific,
                label = {
                    Text(
                        text = if (isScientificExpanded) "Basic Keypad" else "Scientific (fx)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (isScientificExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.Functions,
                        contentDescription = "Scientific functions toggle",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .height(32.dp)
                    .testTag("toggle_scientific_chip"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        // Row 1: C, (), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalcButton(
                text = "C",
                onClick = onClear,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = funcBg,
                contentColor = CalcErrorRed,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                hapticEnabled = hapticEnabled,
                testTag = "btn_clear"
            )
            CalcButton(
                text = "( )",
                onClick = onParenthesis,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = funcBg,
                contentColor = funcText,
                fontSize = 22.sp,
                hapticEnabled = hapticEnabled,
                testTag = "btn_parenthesis"
            )
            CalcButton(
                text = "%",
                onClick = onPercent,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = funcBg,
                contentColor = funcText,
                fontSize = 22.sp,
                hapticEnabled = hapticEnabled,
                testTag = "btn_percent"
            )
            CalcButton(
                text = "÷",
                onClick = { onOperator("/") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalcButton(
                text = "7",
                onClick = { onDigit("7") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_7"
            )
            CalcButton(
                text = "8",
                onClick = { onDigit("8") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_8"
            )
            CalcButton(
                text = "9",
                onClick = { onDigit("9") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_9"
            )
            CalcButton(
                text = "×",
                onClick = { onOperator("*") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalcButton(
                text = "4",
                onClick = { onDigit("4") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_4"
            )
            CalcButton(
                text = "5",
                onClick = { onDigit("5") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_5"
            )
            CalcButton(
                text = "6",
                onClick = { onDigit("6") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_6"
            )
            CalcButton(
                text = "−",
                onClick = { onOperator("-") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalcButton(
                text = "1",
                onClick = { onDigit("1") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_1"
            )
            CalcButton(
                text = "2",
                onClick = { onDigit("2") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_2"
            )
            CalcButton(
                text = "3",
                onClick = { onDigit("3") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_3"
            )
            CalcButton(
                text = "+",
                onClick = { onOperator("+") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_add"
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalcButton(
                text = "±",
                onClick = onToggleSign,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                fontSize = 22.sp,
                hapticEnabled = hapticEnabled,
                testTag = "btn_sign"
            )
            CalcButton(
                text = "0",
                onClick = { onDigit("0") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                hapticEnabled = hapticEnabled,
                testTag = "btn_0"
            )
            CalcButton(
                text = ".",
                onClick = onDecimal,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = digitBg,
                contentColor = digitText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_dot"
            )
            CalcButton(
                text = "=",
                onClick = onEquals,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = equalsBg,
                contentColor = equalsText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                hapticEnabled = hapticEnabled,
                testTag = "btn_equals"
            )
        }
    }
}
