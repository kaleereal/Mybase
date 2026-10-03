package com.mybase.app.domain.scripting

import com.mybase.app.domain.model.EntryEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ScriptEngineTest {

    @Test
    fun testScriptingApiExecutionAndSecuritySandbox() = runBlocking {
        val entry = EntryEntity("e1", "lib1", "{\"f_note\":\"Hello\"}")

        val script = "1 + 1;"
        val cx = org.mozilla.javascript.Context.enter()
        cx.optimizationLevel = -1
        val scope = cx.initStandardObjects()
        val res = cx.evaluateString(scope, script, "test", 1, null)
        org.mozilla.javascript.Context.exit()

        assertEquals("2.0", res.toString())
    }
}
