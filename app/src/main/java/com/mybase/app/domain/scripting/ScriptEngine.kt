package com.mybase.app.domain.scripting

import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.field.EntryManager
import com.mybase.app.domain.model.EntryEntity
import kotlinx.coroutines.runBlocking
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject

class MyBaseScriptApi(
    private val repository: MyBaseRepository,
    private val entryManager: EntryManager
) {
    fun getEntry(entryId: String): String {
        return runBlocking {
            repository.getEntryById(entryId)?.fieldValuesJson ?: "{}"
        }
    }

    fun updateField(libraryId: String, entryId: String, fieldId: String, value: String): String {
        return runBlocking {
            val existing = repository.getEntryById(entryId)
            val currentMap = if (existing != null) {
                com.mybase.app.domain.data.DataEngine.parseValues(existing.fieldValuesJson).toMutableMap()
            } else {
                mutableMapOf()
            }
            currentMap[fieldId] = value
            val result = entryManager.saveEntry(libraryId, entryId, currentMap)
            if (result.isSuccess) "SUCCESS" else "FAILURE"
        }
    }

    fun createEntry(libraryId: String): String {
        return runBlocking {
            val res = entryManager.saveEntry(libraryId, rawValues = emptyMap())
            res.getOrNull()?.id ?: ""
        }
    }
}

object ScriptEngine {

    fun executeScript(
        jsCode: String,
        repository: MyBaseRepository,
        entryManager: EntryManager
    ): String {
        val cx = Context.enter()
        cx.optimizationLevel = -1

        return try {
            val scope = cx.initStandardObjects()

            val api = MyBaseScriptApi(repository, entryManager)
            val jsApi = Context.javaToJS(api, scope)
            ScriptableObject.putProperty(scope, "MyBase", jsApi)

            ScriptableObject.deleteProperty(scope, "Packages")
            ScriptableObject.deleteProperty(scope, "java")
            ScriptableObject.deleteProperty(scope, "javax")

            val result = cx.evaluateString(scope, jsCode, "MyBaseScript", 1, null)
            Context.toString(result)
        } catch (e: Exception) {
            "Error: ${e.message}"
        } finally {
            Context.exit()
        }
    }
}
