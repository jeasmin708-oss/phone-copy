package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object ExpressionEvaluator {

    sealed class EvalResult {
        data class Success(val value: Double, val formatted: String) : EvalResult()
        data class Error(val message: String) : EvalResult()
    }

    /**
     * Evaluates a mathematical expression string.
     * @param expr The expression string (e.g. "12 + 5 × 3", "sin(30) + √16")
     * @param isDegreeMode Whether trigonometric functions take degree arguments
     */
    fun evaluate(expr: String, isDegreeMode: Boolean = true): EvalResult {
        val sanitized = sanitize(expr)
        if (sanitized.isBlank()) return EvalResult.Error("Empty expression")

        return try {
            val parser = Parser(sanitized, isDegreeMode)
            val result = parser.parse()
            if (result.isNaN()) {
                EvalResult.Error("Undefined")
            } else if (result.isInfinite()) {
                EvalResult.Error("Cannot divide by 0")
            } else {
                EvalResult.Success(result, formatResult(result))
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error(e.message ?: "Math error")
        } catch (e: Exception) {
            EvalResult.Error("Invalid syntax")
        }
    }

    /**
     * Attempts a live evaluation for the preview display. Returns null if invalid or incomplete.
     */
    fun evaluatePreview(expr: String, isDegreeMode: Boolean = true): String? {
        val trimmed = expr.trim()
        if (trimmed.isEmpty()) return null
        // Don't show preview for a single plain number
        if (trimmed.toDoubleOrNull() != null) return null

        return when (val res = evaluate(trimmed, isDegreeMode)) {
            is EvalResult.Success -> res.formatted
            is EvalResult.Error -> null
        }
    }

    private fun sanitize(input: String): String {
        return input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("–", "-")
            .replace("π", "PI")
            .replace("e", "E")
            .replace(" ", "")
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        val absVal = abs(value)
        if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-6)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value)
        }

        // Clean precision to avoid IEEE 754 float artifacts (e.g., 0.1 + 0.2 = 0.3)
        return try {
            val bd = BigDecimal(value.toString(), MathContext(12, RoundingMode.HALF_UP))
            val stripped = bd.stripTrailingZeros()
            val plain = stripped.toPlainString()

            // Format with thousand commas before decimal
            val parts = plain.split(".")
            val intPart = parts[0]
            val decPart = if (parts.size > 1) "." + parts[1] else ""

            val isNeg = intPart.startsWith("-")
            val digits = if (isNeg) intPart.substring(1) else intPart
            val formattedInt = digits.reversed().chunked(3).joinToString(",").reversed()
            val prefix = if (isNeg) "-" else ""

            prefix + formattedInt + decPart
        } catch (e: Exception) {
            val df = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US))
            df.format(value)
        }
    }

    private class Parser(private val text: String, private val isDegreeMode: Boolean) {
        private var pos = -1
        private var ch = ' '

        private fun nextChar() {
            pos++
            ch = if (pos < text.length) text[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < text.length && ch != '\u0000' && ch != ')') {
                throw IllegalArgumentException("Unexpected: $ch")
            }
            return x
        }

        // Grammar:
        // Expression = Term | Expression + Term | Expression - Term
        // Term = Factor | Term * Factor | Term / Factor | Term % Factor
        // Factor = Base ^ Factor | Base
        // Base = +Factor | -Factor | Primary
        // Primary = Number | Constant | Function | ( Expression )

        private data class TermResult(val value: Double, val hadPercent: Boolean)
        private data class FactorResult(val value: Double, val hadPercent: Boolean)

        private fun parseExpression(): Double {
            var term = parseTerm()
            var x = if (term.hadPercent) term.value / 100.0 else term.value
            while (true) {
                when {
                    eat('+') -> {
                        val next = parseTerm()
                        val addend = if (next.hadPercent) x * (next.value / 100.0) else next.value
                        x += addend
                    }
                    eat('-') -> {
                        val next = parseTerm()
                        val subtrahend = if (next.hadPercent) x * (next.value / 100.0) else next.value
                        x -= subtrahend
                    }
                    else -> return x
                }
            }
        }

        private fun parseTerm(): TermResult {
            val firstFactor = parseFactor()
            var x = if (firstFactor.hadPercent) firstFactor.value / 100.0 else firstFactor.value
            var lastHadPercent = firstFactor.hadPercent

            while (true) {
                when {
                    eat('*') -> {
                        val factor = parseFactor()
                        val factorVal = if (factor.hadPercent) factor.value / 100.0 else factor.value
                        x *= factorVal
                        lastHadPercent = false
                    }
                    eat('/') -> {
                        val factor = parseFactor()
                        val factorVal = if (factor.hadPercent) factor.value / 100.0 else factor.value
                        if (factorVal == 0.0) throw ArithmeticException("Cannot divide by 0")
                        x /= factorVal
                        lastHadPercent = false
                    }
                    else -> return TermResult(if (lastHadPercent) firstFactor.value else x, lastHadPercent)
                }
            }
        }

        private fun parseFactor(): FactorResult {
            val base = parseUnary()
            var x = base
            if (eat('^')) {
                val exponent = parseFactor() // Right associative
                val expVal = if (exponent.hadPercent) exponent.value / 100.0 else exponent.value
                x = x.pow(expVal)
            }
            if (eat('!')) {
                x = factorial(x)
            }
            val hasPercent = eat('%')
            return FactorResult(x, hasPercent)
        }

        private fun parseUnary(): Double {
            if (eat('+')) return parseUnary()
            if (eat('-')) return -parseUnary()
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val startPos = pos
            if (eat('(')) {
                val x = parseExpression()
                eat(')') // auto-close if omitted at end
                if (eat('!')) return factorial(x)
                return x
            }

            // Constants
            if (eatString("PI")) {
                var v = Math.PI
                if (eat('!')) v = factorial(v)
                return v
            }
            if (eatString("E")) {
                var v = Math.E
                if (eat('!')) v = factorial(v)
                return v
            }

            // Functions
            val func = parseIdentifier()
            if (func.isNotEmpty()) {
                val arg = if (eat('(')) {
                    val inner = parseExpression()
                    eat(')')
                    inner
                } else {
                    parsePrimary()
                }

                var res = evaluateFunction(func, arg)
                if (eat('!')) res = factorial(res)
                return res
            }

            // Numbers
            if ((ch in '0'..'9') || ch == '.') {
                while ((ch in '0'..'9') || ch == '.') nextChar()
                val numStr = text.substring(startPos, pos)
                var num = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Bad number: $numStr")
                if (eat('!')) num = factorial(num)
                return num
            }

            if (ch == '\u0000') {
                return 0.0
            }

            throw IllegalArgumentException("Unexpected char: '$ch' at pos $pos")
        }

        private fun parseIdentifier(): String {
            val startPos = pos
            while (ch in 'a'..'z' || ch in 'A'..'Z' || ch == '√') {
                nextChar()
            }
            return if (pos > startPos) text.substring(startPos, pos) else ""
        }

        private fun eatString(s: String): Boolean {
            if (text.startsWith(s, pos)) {
                pos += s.length
                ch = if (pos < text.length) text[pos] else '\u0000'
                return true
            }
            return false
        }

        private fun evaluateFunction(func: String, arg: Double): Double {
            return when (func.lowercase()) {
                "sin" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    sin(rad)
                }
                "cos" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    cos(rad)
                }
                "tan" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    val t = tan(rad)
                    if (abs(t) > 1e15) throw ArithmeticException("Undefined tan")
                    t
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    val rad = asin(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    val rad = acos(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "atan" -> {
                    val rad = atan(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "ln" -> {
                    if (arg <= 0.0) throw ArithmeticException("Domain error")
                    ln(arg)
                }
                "log" -> {
                    if (arg <= 0.0) throw ArithmeticException("Domain error")
                    log10(arg)
                }
                "sqrt", "√" -> {
                    if (arg < 0.0) throw ArithmeticException("Cannot take sqrt of negative number")
                    sqrt(arg)
                }
                "abs" -> abs(arg)
                else -> throw IllegalArgumentException("Unknown function: $func")
            }
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != kotlin.math.floor(n) || n > 170) {
                throw ArithmeticException("Invalid factorial input")
            }
            var result = 1.0
            val intN = n.toInt()
            for (i in 2..intN) {
                result *= i
            }
            return result
        }
    }
}
