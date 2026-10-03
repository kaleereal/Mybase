package com.mybase.app.domain.formula

import com.mybase.app.domain.model.EntryEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class FormulaEngineTest {

    @Test
    fun testMathFormulaEvaluation() {
        val entry = EntryEntity(
            id = "e1",
            libraryId = "lib1",
            fieldValuesJson = "{\"f_price\":\"15.5\", \"f_qty\":\"2\"}"
        )

        val result = FormulaEngine.evaluate("{f_price} * {f_qty}", entry)
        assertEquals("31", result)
    }

    @Test
    fun testMissingFieldFallback() {
        val entry = EntryEntity(
            id = "e1",
            libraryId = "lib1",
            fieldValuesJson = "{}"
        )

        val result = FormulaEngine.evaluate("{f_missing} + 5", entry)
        assertEquals("5", result)
    }
}
