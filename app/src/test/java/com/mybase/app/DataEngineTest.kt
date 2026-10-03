package com.mybase.app.domain.data

import com.mybase.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class DataEngineTest {

    @Test
    fun testSearchAndFilterAndSort() {
        val fTitle = FieldEntity("f1", "lib1", "Title", FieldType.TEXT)
        val fPrice = FieldEntity("f2", "lib1", "Price", FieldType.NUMBER)

        val e1 = EntryEntity("e1", "lib1", "{\"f1\":\"Apple\", \"f2\":\"10\"}")
        val e2 = EntryEntity("e2", "lib1", "{\"f1\":\"Banana\", \"f2\":\"20\"}")
        val e3 = EntryEntity("e3", "lib1", "{\"f1\":\"Avocado\", \"f2\":\"15\"}")

        val entries = listOf(e1, e2, e3)
        val fields = mapOf("f1" to fTitle, "f2" to fPrice)

        val searchRes = DataEngine.filterEntries(entries, fields, null, searchQuery = "A")
        assertEquals(3, searchRes.size)

        val searchResExact = DataEngine.filterEntries(entries, fields, null, searchQuery = "Avo")
        assertEquals(1, searchResExact.size)

        val filterGroup = FilterGroup(
            rules = listOf(FilterRule("f2", FilterOperator.GREATER_THAN, "12"))
        )
        val filterRes = DataEngine.filterEntries(entries, fields, filterGroup)
        assertEquals(2, filterRes.size)

        val sortRules = listOf(SortRule("f2", isAscending = false))
        val sortRes = DataEngine.sortEntries(entries, sortRules)
        assertEquals("e2", sortRes[0].id)
        assertEquals("e3", sortRes[1].id)
        assertEquals("e1", sortRes[2].id)
    }
}
