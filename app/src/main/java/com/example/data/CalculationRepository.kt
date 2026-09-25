package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val history: Flow<List<CalculationEntity>> = dao.getAll()

    suspend fun addCalculation(expression: String, result: String) {
        if (expression.isBlank() || result.isBlank()) return
        dao.insert(CalculationEntity(expression = expression.trim(), result = result.trim()))
    }

    suspend fun deleteCalculation(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }
}
