package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.converter.UnitCategory
import com.example.converter.UnitConverter
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.CalculationRepository
import com.example.engine.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CalculatorMode {
    STANDARD,
    SCIENTIFIC
}

enum class ActiveTab {
    CALCULATOR,
    CONVERTER
}

data class ConverterState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val fromIndex: Int = 0,
    val toIndex: Int = 1,
    val inputString: String = "1",
    val resultString: String = ""
)

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val finalResult: String? = null,
    val lastEvaluatedExpression: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isDegreeMode: Boolean = true,
    val isScientificExpanded: Boolean = false,
    val memoryValue: Double? = null,
    val activeTab: ActiveTab = ActiveTab.CALCULATOR,
    val hapticEnabled: Boolean = true,
    val isHistoryOpen: Boolean = false,
    val converterState: ConverterState = ConverterState()
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalculationRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CalculationRepository(database.calculationDao())
    }

    val historyItems: StateFlow<List<CalculationEntity>> = repository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        updateConverterCalculation()
    }

    // --- Calculator Actions ---

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.finalResult != null && !state.isError) {
                // If a previous result is present and user presses a digit, start fresh
                digit
            } else {
                state.expression + digit
            }
            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            val baseExpr = if (state.finalResult != null && !state.isError) {
                // Chain off previous result
                state.finalResult.replace(",", "")
            } else {
                state.expression
            }

            val formattedOp = when (op) {
                "+" -> " + "
                "-" -> " − "
                "*" -> " × "
                "/" -> " ÷ "
                else -> " $op "
            }

            // If expression ends with an operator, replace it
            val trimmed = baseExpr.trimEnd()
            val newExpr = if (trimmed.endsWith("+") || trimmed.endsWith("−") || trimmed.endsWith("×") || trimmed.endsWith("÷")) {
                trimmed.dropLast(1).trimEnd() + formattedOp
            } else if (baseExpr.isEmpty()) {
                if (op == "-") "−" else ""
            } else {
                baseExpr + formattedOp
            }

            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onDecimal() {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && !state.isError) "" else state.expression
            val lastToken = expr.split(' ', '+', '−', '×', '÷', '(', ')', '^').lastOrNull() ?: ""

            val newExpr = if (lastToken.contains(".")) {
                expr // already has decimal in this number
            } else if (lastToken.isEmpty() || expr.endsWith(" ") || expr.endsWith("(") || expr.isEmpty()) {
                expr + "0."
            } else {
                expr + "."
            }

            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onParenthesis() {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && !state.isError) "" else state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val lastChar = expr.lastOrNull()
            val shouldClose = openCount > closeCount && lastChar != null &&
                    (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '%')

            val newExpr = if (shouldClose) {
                expr + ")"
            } else {
                if (lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
                    expr + " × ("
                } else {
                    expr + "("
                }
            }

            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onPercent() {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && !state.isError) {
                state.finalResult.replace(",", "")
            } else {
                state.expression
            }

            if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) return@update state

            val newExpr = "$expr%"
            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onToggleSign() {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && !state.isError) {
                state.finalResult.replace(",", "")
            } else {
                state.expression
            }

            if (expr.isEmpty()) {
                return@update state.copy(expression = "−")
            }

            // Find start of last number or parenthesis
            val regex = Regex("""([−\-]?[0-9.]+|π|e)$""")
            val match = regex.find(expr)

            val newExpr = if (match != null) {
                val token = match.value
                val toggled = if (token.startsWith("−") || token.startsWith("-")) {
                    token.substring(1)
                } else {
                    "−$token"
                }
                expr.substring(0, match.range.first) + toggled
            } else {
                expr
            }

            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onScientificFunction(func: String) {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && !state.isError) {
                state.finalResult.replace(",", "")
            } else {
                state.expression
            }

            val lastChar = expr.lastOrNull()
            val needMultiply = lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')
            val prefix = if (needMultiply) " × " else ""

            val newExpr = when (func) {
                "x²" -> if (expr.isNotEmpty() && !expr.endsWith(" ")) "$expr^2" else expr
                "x^y" -> if (expr.isNotEmpty() && !expr.endsWith(" ")) "$expr^" else expr
                "1/x" -> if (expr.isNotEmpty() && !expr.endsWith(" ")) "1 ÷ ($expr)" else "1 ÷ ("
                "!" -> if (expr.isNotEmpty() && !expr.endsWith(" ")) "$expr!" else expr
                "π" -> "$expr${prefix}π"
                "e" -> "$expr${prefix}e"
                "√" -> "$expr${prefix}√("
                else -> "$expr${prefix}${func}("
            }

            state.copy(
                expression = newExpr,
                finalResult = null,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.finalResult != null) {
                return@update state.copy(expression = "", finalResult = null, previewResult = null)
            }
            if (state.expression.isEmpty()) return@update state

            val expr = state.expression
            val newExpr = when {
                expr.endsWith(" + ") || expr.endsWith(" − ") || expr.endsWith(" × ") || expr.endsWith(" ÷ ") -> {
                    expr.dropLast(3)
                }
                expr.endsWith("sin(") || expr.endsWith("cos(") || expr.endsWith("tan(") || expr.endsWith("log(") -> {
                    expr.dropLast(4)
                }
                expr.endsWith("asin(") || expr.endsWith("acos(") || expr.endsWith("atan(") || expr.endsWith("sqrt(") -> {
                    expr.dropLast(5)
                }
                expr.endsWith("ln(") || expr.endsWith("√(") -> {
                    expr.dropLast(if (expr.endsWith("ln(")) 3 else 2)
                }
                else -> expr.dropLast(1)
            }

            state.copy(
                expression = newExpr,
                isError = false,
                errorMessage = null,
                previewResult = ExpressionEvaluator.evaluatePreview(newExpr, state.isDegreeMode)
            )
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = null,
                finalResult = null,
                isError = false,
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression.trim()
        if (expr.isEmpty()) return

        when (val eval = ExpressionEvaluator.evaluate(expr, state.isDegreeMode)) {
            is ExpressionEvaluator.EvalResult.Success -> {
                val formatted = eval.formatted
                _uiState.update {
                    it.copy(
                        finalResult = formatted,
                        lastEvaluatedExpression = expr,
                        previewResult = null,
                        isError = false,
                        errorMessage = null
                    )
                }
                // Save to history in Room DB
                viewModelScope.launch {
                    repository.addCalculation(expr, formatted)
                }
            }
            is ExpressionEvaluator.EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorMessage = eval.message,
                        previewResult = null
                    )
                }
            }
        }
    }

    // --- History actions ---

    fun onSelectHistoryItem(item: CalculationEntity) {
        _uiState.update {
            it.copy(
                expression = item.expression,
                finalResult = item.result,
                isHistoryOpen = false,
                isError = false,
                errorMessage = null
            )
        }
    }

    fun onReuseResult(result: String) {
        _uiState.update {
            it.copy(
                expression = result.replace(",", ""),
                finalResult = null,
                isHistoryOpen = false,
                isError = false,
                errorMessage = null
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun setHistoryOpen(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    // --- Mode / Settings toggles ---

    fun toggleAngleMode() {
        _uiState.update {
            val newDegreeMode = !it.isDegreeMode
            it.copy(
                isDegreeMode = newDegreeMode,
                previewResult = ExpressionEvaluator.evaluatePreview(it.expression, newDegreeMode)
            )
        }
    }

    fun toggleScientificExpanded() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun toggleHaptic() {
        _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
    }

    fun setActiveTab(tab: ActiveTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    // --- Memory Functions ---

    fun memoryClear() {
        _uiState.update { it.copy(memoryValue = null) }
    }

    fun memoryRecall() {
        val mem = _uiState.value.memoryValue ?: return
        val formatted = ExpressionEvaluator.formatResult(mem).replace(",", "")
        _uiState.update { state ->
            state.copy(
                expression = state.expression + formatted,
                previewResult = ExpressionEvaluator.evaluatePreview(state.expression + formatted, state.isDegreeMode)
            )
        }
    }

    fun memoryAdd() {
        val currentVal = getCurrentNumericValue() ?: return
        _uiState.update {
            val oldMem = it.memoryValue ?: 0.0
            it.copy(memoryValue = oldMem + currentVal)
        }
    }

    fun memorySubtract() {
        val currentVal = getCurrentNumericValue() ?: return
        _uiState.update {
            val oldMem = it.memoryValue ?: 0.0
            it.copy(memoryValue = oldMem - currentVal)
        }
    }

    private fun getCurrentNumericValue(): Double? {
        val state = _uiState.value
        val target = state.finalResult ?: state.previewResult ?: state.expression
        return when (val eval = ExpressionEvaluator.evaluate(target, state.isDegreeMode)) {
            is ExpressionEvaluator.EvalResult.Success -> eval.value
            is ExpressionEvaluator.EvalResult.Error -> null
        }
    }

    // --- Unit Converter Actions ---

    fun setConverterCategory(category: UnitCategory) {
        _uiState.update {
            it.copy(
                converterState = it.converterState.copy(
                    category = category,
                    fromIndex = 0,
                    toIndex = if (UnitConverter.categoryUnits[category]?.size ?: 0 > 1) 1 else 0
                )
            )
        }
        updateConverterCalculation()
    }

    fun setConverterFromUnit(index: Int) {
        _uiState.update {
            it.copy(converterState = it.converterState.copy(fromIndex = index))
        }
        updateConverterCalculation()
    }

    fun setConverterToUnit(index: Int) {
        _uiState.update {
            it.copy(converterState = it.converterState.copy(toIndex = index))
        }
        updateConverterCalculation()
    }

    fun swapConverterUnits() {
        _uiState.update {
            val cur = it.converterState
            it.copy(converterState = cur.copy(fromIndex = cur.toIndex, toIndex = cur.fromIndex))
        }
        updateConverterCalculation()
    }

    fun onConverterDigit(digit: String) {
        _uiState.update {
            val current = it.converterState.inputString
            val newInput = if (current == "0") digit else current + digit
            it.copy(converterState = it.converterState.copy(inputString = newInput))
        }
        updateConverterCalculation()
    }

    fun onConverterDecimal() {
        _uiState.update {
            val current = it.converterState.inputString
            val newInput = if (!current.contains(".")) {
                if (current.isEmpty()) "0." else "$current."
            } else {
                current
            }
            it.copy(converterState = it.converterState.copy(inputString = newInput))
        }
        updateConverterCalculation()
    }

    fun onConverterBackspace() {
        _uiState.update {
            val current = it.converterState.inputString
            val newInput = if (current.length > 1) current.dropLast(1) else "0"
            it.copy(converterState = it.converterState.copy(inputString = newInput))
        }
        updateConverterCalculation()
    }

    fun onConverterClear() {
        _uiState.update {
            it.copy(converterState = it.converterState.copy(inputString = "0"))
        }
        updateConverterCalculation()
    }

    private fun updateConverterCalculation() {
        _uiState.update {
            val conv = it.converterState
            val inputVal = conv.inputString.toDoubleOrNull() ?: 0.0
            val converted = UnitConverter.convert(conv.category, conv.fromIndex, conv.toIndex, inputVal)
            it.copy(
                converterState = conv.copy(resultString = UnitConverter.formatConvertedValue(converted))
            )
        }
    }
}
