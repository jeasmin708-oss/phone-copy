package com.example.converter

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Mass & Weight"),
    TEMPERATURE("Temperature"),
    SPEED("Speed"),
    DATA("Digital Storage"),
    AREA("Area"),
    VOLUME("Volume")
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double
)

object UnitConverter {

    val categories: List<UnitCategory> = UnitCategory.values().toList()

    val categoryUnits: Map<UnitCategory, List<ConversionUnit>> = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("Meters", "m", { it }, { it }),
            ConversionUnit("Kilometers", "km", { it * 1000.0 }, { it / 1000.0 }),
            ConversionUnit("Centimeters", "cm", { it * 0.01 }, { it / 0.01 }),
            ConversionUnit("Millimeters", "mm", { it * 0.001 }, { it / 0.001 }),
            ConversionUnit("Miles", "mi", { it * 1609.344 }, { it / 1609.344 }),
            ConversionUnit("Yards", "yd", { it * 0.9144 }, { it / 0.9144 }),
            ConversionUnit("Feet", "ft", { it * 0.3048 }, { it / 0.3048 }),
            ConversionUnit("Inches", "in", { it * 0.0254 }, { it / 0.0254 })
        ),
        UnitCategory.WEIGHT to listOf(
            ConversionUnit("Kilograms", "kg", { it }, { it }),
            ConversionUnit("Grams", "g", { it * 0.001 }, { it / 0.001 }),
            ConversionUnit("Milligrams", "mg", { it * 0.000001 }, { it / 0.000001 }),
            ConversionUnit("Pounds", "lb", { it * 0.45359237 }, { it / 0.45359237 }),
            ConversionUnit("Ounces", "oz", { it * 0.02834952 }, { it / 0.02834952 }),
            ConversionUnit("Metric Tons", "t", { it * 1000.0 }, { it / 1000.0 })
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("Celsius", "°C", { it }, { it }),
            ConversionUnit("Fahrenheit", "°F", { (it - 32.0) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32.0 }),
            ConversionUnit("Kelvin", "K", { it - 273.15 }, { it + 273.15 })
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("Kilometers/hour", "km/h", { it / 3.6 }, { it * 3.6 }),
            ConversionUnit("Miles/hour", "mph", { it * 0.44704 }, { it / 0.44704 }),
            ConversionUnit("Meters/second", "m/s", { it }, { it }),
            ConversionUnit("Knots", "kn", { it * 0.514444 }, { it / 0.514444 })
        ),
        UnitCategory.DATA to listOf(
            ConversionUnit("Megabytes", "MB", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            ConversionUnit("Gigabytes", "GB", { it * 1_000_000_000.0 }, { it / 1_000_000_000.0 }),
            ConversionUnit("Terabytes", "TB", { it * 1_000_000_000_000.0 }, { it / 1_000_000_000_000.0 }),
            ConversionUnit("Kilobytes", "KB", { it * 1_000.0 }, { it / 1_000.0 }),
            ConversionUnit("Bytes", "B", { it }, { it }),
            ConversionUnit("Gibibytes", "GiB", { it * 1073741824.0 }, { it / 1073741824.0 }),
            ConversionUnit("Mebibytes", "MiB", { it * 1048576.0 }, { it / 1048576.0 })
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("Square Meters", "m²", { it }, { it }),
            ConversionUnit("Square Kilometers", "km²", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            ConversionUnit("Square Feet", "ft²", { it * 0.092903 }, { it / 0.092903 }),
            ConversionUnit("Acres", "ac", { it * 4046.85642 }, { it / 4046.85642 }),
            ConversionUnit("Hectares", "ha", { it * 10000.0 }, { it / 10000.0 })
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("Liters", "L", { it }, { it }),
            ConversionUnit("Milliliters", "mL", { it * 0.001 }, { it / 0.001 }),
            ConversionUnit("Gallons (US)", "gal", { it * 3.78541 }, { it / 3.78541 }),
            ConversionUnit("Fluid Ounces (US)", "fl oz", { it * 0.0295735 }, { it / 0.0295735 }),
            ConversionUnit("Cups", "cup", { it * 0.236588 }, { it / 0.236588 })
        )
    )

    fun convert(
        category: UnitCategory,
        fromIndex: Int,
        toIndex: Int,
        value: Double
    ): Double {
        val units = categoryUnits[category] ?: return value
        val fromUnit = units.getOrNull(fromIndex) ?: return value
        val toUnit = units.getOrNull(toIndex) ?: return value
        val baseValue = fromUnit.toBase(value)
        return toUnit.fromBase(baseValue)
    }

    fun formatConvertedValue(value: Double): String {
        val df = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US))
        return df.format(value)
    }
}
