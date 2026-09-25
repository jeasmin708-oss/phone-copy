package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.converter.UnitCategory
import com.example.converter.UnitConverter
import com.example.ui.ConverterState
import com.example.ui.theme.CalcAccentAmber
import com.example.ui.theme.CalcErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterView(
    state: ConverterState,
    hapticEnabled: Boolean,
    onCategorySelected: (UnitCategory) -> Unit,
    onFromUnitSelected: (Int) -> Unit,
    onToUnitSelected: (Int) -> Unit,
    onSwapUnits: () -> Unit,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryScrollState = rememberScrollState()
    val availableUnits = UnitConverter.categoryUnits[state.category] ?: emptyList()

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(categoryScrollState)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UnitConverter.categories.forEach { cat ->
                    val isSelected = cat == state.category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(cat) },
                        label = { Text(cat.displayName, fontSize = 13.sp) },
                        modifier = Modifier.testTag("converter_chip_${cat.name}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Dual Conversion Cards (From & To)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // FROM Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dropdown for From Unit
                        ExposedDropdownMenuBox(
                            expanded = fromExpanded,
                            onExpandedChange = { fromExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            val currentFrom = availableUnits.getOrNull(state.fromIndex)?.name ?: "Select"
                            OutlinedTextField(
                                value = currentFrom,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("From") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = fromExpanded,
                                onDismissRequest = { fromExpanded = false }
                            ) {
                                availableUnits.forEachIndexed { idx, unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            onFromUnitSelected(idx)
                                            fromExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Swap Button
                        IconButton(
                            onClick = onSwapUnits,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(44.dp)
                                .testTag("swap_units_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap units",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Dropdown for To Unit
                        ExposedDropdownMenuBox(
                            expanded = toExpanded,
                            onExpandedChange = { toExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            val currentTo = availableUnits.getOrNull(state.toIndex)?.name ?: "Select"
                            OutlinedTextField(
                                value = currentTo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("To") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = toExpanded,
                                onDismissRequest = { toExpanded = false }
                            ) {
                                availableUnits.forEachIndexed { idx, unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            onToUnitSelected(idx)
                                            toExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input & Result Displays
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val fromSymbol = availableUnits.getOrNull(state.fromIndex)?.symbol ?: ""
                            Text(
                                text = "Input: ${state.inputString} $fromSymbol",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val toSymbol = availableUnits.getOrNull(state.toIndex)?.symbol ?: ""
                            Text(
                                text = "${state.resultString} $toSymbol",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalcAccentAmber,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                val toSymbol = availableUnits.getOrNull(state.toIndex)?.symbol ?: ""
                                val text = "${state.resultString} $toSymbol"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Converted Value", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy converted value",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Numeric Keypad for Unit Converter
        val numpadHeight = 52.dp
        val spacing = 8.dp
        val keyBg = MaterialTheme.colorScheme.surfaceVariant
        val keyText = MaterialTheme.colorScheme.onSurface

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                CalcButton("7", { onDigit("7") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("8", { onDigit("8") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("9", { onDigit("9") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("C", onClear, Modifier.weight(1f).height(numpadHeight), keyBg.copy(alpha = 0.7f), CalcErrorRed, fontWeight = FontWeight.Bold, hapticEnabled = hapticEnabled)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                CalcButton("4", { onDigit("4") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("5", { onDigit("5") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("6", { onDigit("6") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton(
                    text = "⌫",
                    onClick = onBackspace,
                    modifier = Modifier.weight(1f).height(numpadHeight),
                    backgroundColor = keyBg.copy(alpha = 0.7f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    hapticEnabled = hapticEnabled,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                CalcButton("1", { onDigit("1") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("2", { onDigit("2") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("3", { onDigit("3") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton(".", onDecimal, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, fontWeight = FontWeight.Bold, hapticEnabled = hapticEnabled)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                CalcButton("0", { onDigit("0") }, Modifier.weight(2f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("00", { onDigit("00") }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
                CalcButton("±", {
                    val cur = state.inputString
                    val toggled = if (cur.startsWith("-")) cur.substring(1) else "-$cur"
                    // Trigger custom input
                    onDigit("")
                }, Modifier.weight(1f).height(numpadHeight), keyBg, keyText, hapticEnabled = hapticEnabled)
            }
        }
    }
}
