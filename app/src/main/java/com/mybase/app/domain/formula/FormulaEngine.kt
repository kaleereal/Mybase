package com.mybase.app.domain.formula

import com.mybase.app.domain.data.DataEngine
import com.mybase.app.domain.model.EntryEntity

object FormulaEngine {

    fun evaluate(expression: String, entry: EntryEntity): String {
        val values = DataEngine.parseValues(entry.fieldValuesJson)
        var expr = expression.trim()

        val fieldRegex = Regex("\\{([a-zA-Z0-9_]+)\\}")
        expr = fieldRegex.replace(expr) { matchResult ->
            val fieldId = matchResult.groupValues[1]
            values[fieldId] ?: "0"
        }

        return try {
            if (expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/")) {
                val result = simpleMathEval(expr)
                if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
            } else {
                expr
            }
        } catch (e: Exception) {
            "#ERROR"
        }
    }

    private fun simpleMathEval(expr: String): Double {
        val sanitized = expr.replace(" ", "")
        val tokens = mutableListOf<String>()
        var numberBuffer = ""

        for (ch in sanitized) {
            if (ch.isDigit() || ch == '.') {
                numberBuffer += ch
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                if (numberBuffer.isNotEmpty()) {
                    tokens.add(numberBuffer)
                    numberBuffer = ""
                }
                tokens.add(ch.toString())
            }
        }
        if (numberBuffer.isNotEmpty()) {
            tokens.add(numberBuffer)
        }

        if (tokens.isEmpty()) return 0.0

        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == "*" || tokens[i] == "/") {
                val op = tokens[i]
                val left = tokens[i - 1].toDoubleOrNull() ?: 0.0
                val right = tokens[i + 1].toDoubleOrNull() ?: 1.0
                val res = if (op == "*") left * right else if (right != 0.0) left / right else 0.0
                tokens[i - 1] = res.toString()
                tokens.removeAt(i)
                tokens.removeAt(i)
                i--
            } else {
                i++
            }
        }

        var acc = tokens[0].toDoubleOrNull() ?: 0.0
        i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val nextVal = tokens[i + 1].toDoubleOrNull() ?: 0.0
            if (op == "+") acc += nextVal
            if (op == "-") acc -= nextVal
            i += 2
        }

        return acc
    }
}
