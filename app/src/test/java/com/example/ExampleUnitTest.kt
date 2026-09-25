package com.example

import com.example.converter.UnitCategory
import com.example.converter.UnitConverter
import com.example.engine.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBasicArithmetic() {
        val res1 = ExpressionEvaluator.evaluate("2 + 2")
        assertTrue(res1 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("4", (res1 as ExpressionEvaluator.EvalResult.Success).formatted)

        val res2 = ExpressionEvaluator.evaluate("10 - 3 × 2")
        assertTrue(res2 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("4", (res2 as ExpressionEvaluator.EvalResult.Success).formatted)

        val res3 = ExpressionEvaluator.evaluate("20 ÷ 4 + 1")
        assertTrue(res3 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("6", (res3 as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testPercentage() {
        // 100 + 10% = 110
        val res1 = ExpressionEvaluator.evaluate("100 + 10%")
        assertTrue(res1 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("110", (res1 as ExpressionEvaluator.EvalResult.Success).formatted)

        // 200 - 25% = 150
        val res2 = ExpressionEvaluator.evaluate("200 - 25%")
        assertTrue(res2 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("150", (res2 as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testParenthesesAndFunctions() {
        val res1 = ExpressionEvaluator.evaluate("(2 + 3) × 4")
        assertTrue(res1 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("20", (res1 as ExpressionEvaluator.EvalResult.Success).formatted)

        val res2 = ExpressionEvaluator.evaluate("√16 + 5")
        assertTrue(res2 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("9", (res2 as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testUnitConverter() {
        // 1 km = 1000 m
        val meters = UnitConverter.convert(UnitCategory.LENGTH, 1, 0, 1.0)
        assertEquals(1000.0, meters, 0.001)

        // 0 C = 32 F
        val fahrenheit = UnitConverter.convert(UnitCategory.TEMPERATURE, 0, 1, 0.0)
        assertEquals(32.0, fahrenheit, 0.001)
    }
}
