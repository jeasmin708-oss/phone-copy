package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DisplaySection
import com.example.ui.components.HistorySheet
import com.example.ui.components.KeypadSection
import com.example.ui.components.ScientificSection
import com.example.ui.components.UnitConverterView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyItems.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Pocket Calc",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Mode Tabs (Calculator vs Unit Converter)
                PrimaryTabRow(
                    selectedTabIndex = if (uiState.activeTab == ActiveTab.CALCULATOR) 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = uiState.activeTab == ActiveTab.CALCULATOR,
                        onClick = { viewModel.setActiveTab(ActiveTab.CALCULATOR) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculator", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_calculator")
                    )
                    Tab(
                        selected = uiState.activeTab == ActiveTab.CONVERTER,
                        onClick = { viewModel.setActiveTab(ActiveTab.CONVERTER) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Converter", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_converter")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
            ) {
                val isWide = maxWidth > 560.dp

                Crossfade(
                    targetState = uiState.activeTab,
                    label = "tab_crossfade"
                ) { tab ->
                    when (tab) {
                        ActiveTab.CALCULATOR -> {
                            if (isWide) {
                                // Wide / Tablet Layout
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .fillMaxHeight()
                                    ) {
                                        DisplaySection(
                                            uiState = uiState,
                                            onAngleToggle = { viewModel.toggleAngleMode() },
                                            onHistoryClick = { viewModel.setHistoryOpen(true) },
                                            onHapticToggle = { viewModel.toggleHaptic() },
                                            onBackspace = { viewModel.onBackspace() },
                                            onClear = { viewModel.onClear() },
                                            modifier = Modifier.weight(1f)
                                        )
                                        ScientificSection(
                                            isExpanded = true,
                                            hapticEnabled = uiState.hapticEnabled,
                                            onScientificFunc = { viewModel.onScientificFunction(it) },
                                            onMemoryClear = { viewModel.memoryClear() },
                                            onMemoryRecall = { viewModel.memoryRecall() },
                                            onMemoryAdd = { viewModel.memoryAdd() }
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        KeypadSection(
                                            isScientificExpanded = false,
                                            hapticEnabled = uiState.hapticEnabled,
                                            onDigit = { viewModel.onDigit(it) },
                                            onOperator = { viewModel.onOperator(it) },
                                            onDecimal = { viewModel.onDecimal() },
                                            onParenthesis = { viewModel.onParenthesis() },
                                            onPercent = { viewModel.onPercent() },
                                            onToggleSign = { viewModel.onToggleSign() },
                                            onClear = { viewModel.onClear() },
                                            onEquals = { viewModel.onEquals() },
                                            onToggleScientific = { viewModel.toggleScientificExpanded() }
                                        )
                                    }
                                }
                            } else {
                                // Standard Handheld Portrait Layout
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    DisplaySection(
                                        uiState = uiState,
                                        onAngleToggle = { viewModel.toggleAngleMode() },
                                        onHistoryClick = { viewModel.setHistoryOpen(true) },
                                        onHapticToggle = { viewModel.toggleHaptic() },
                                        onBackspace = { viewModel.onBackspace() },
                                        onClear = { viewModel.onClear() },
                                        modifier = Modifier.weight(1f)
                                    )

                                    ScientificSection(
                                        isExpanded = uiState.isScientificExpanded,
                                        hapticEnabled = uiState.hapticEnabled,
                                        onScientificFunc = { viewModel.onScientificFunction(it) },
                                        onMemoryClear = { viewModel.memoryClear() },
                                        onMemoryRecall = { viewModel.memoryRecall() },
                                        onMemoryAdd = { viewModel.memoryAdd() }
                                    )

                                    KeypadSection(
                                        isScientificExpanded = uiState.isScientificExpanded,
                                        hapticEnabled = uiState.hapticEnabled,
                                        onDigit = { viewModel.onDigit(it) },
                                        onOperator = { viewModel.onOperator(it) },
                                        onDecimal = { viewModel.onDecimal() },
                                        onParenthesis = { viewModel.onParenthesis() },
                                        onPercent = { viewModel.onPercent() },
                                        onToggleSign = { viewModel.onToggleSign() },
                                        onClear = { viewModel.onClear() },
                                        onEquals = { viewModel.onEquals() },
                                        onToggleScientific = { viewModel.toggleScientificExpanded() }
                                    )
                                }
                            }
                        }

                        ActiveTab.CONVERTER -> {
                            UnitConverterView(
                                state = uiState.converterState,
                                hapticEnabled = uiState.hapticEnabled,
                                onCategorySelected = { viewModel.setConverterCategory(it) },
                                onFromUnitSelected = { viewModel.setConverterFromUnit(it) },
                                onToUnitSelected = { viewModel.setConverterToUnit(it) },
                                onSwapUnits = { viewModel.swapConverterUnits() },
                                onDigit = { viewModel.onConverterDigit(it) },
                                onDecimal = { viewModel.onConverterDecimal() },
                                onBackspace = { viewModel.onConverterBackspace() },
                                onClear = { viewModel.onConverterClear() },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // History Modal Bottom Sheet
        HistorySheet(
            isOpen = uiState.isHistoryOpen,
            historyList = historyList,
            onDismiss = { viewModel.setHistoryOpen(false) },
            onSelectItem = { viewModel.onSelectHistoryItem(it) },
            onReuseResult = { viewModel.onReuseResult(it) },
            onDeleteItem = { viewModel.deleteHistoryItem(it) },
            onClearAll = { viewModel.clearAllHistory() }
        )
    }
}
