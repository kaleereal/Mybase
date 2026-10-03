package com.mybase.app.domain.dashboard

import com.mybase.app.domain.data.DataEngine
import com.mybase.app.domain.model.DashboardWidgetEntity
import com.mybase.app.domain.model.EntryEntity
import com.mybase.app.domain.model.WidgetType

data class WidgetCalculationResult(
    val title: String,
    val type: WidgetType,
    val mainValue: String,
    val items: List<Pair<String, String>> = emptyList()
)

object DashboardEngine {

    fun calculateWidgetData(
        widget: DashboardWidgetEntity,
        entries: List<EntryEntity>
    ): WidgetCalculationResult {
        return when (widget.type) {
            WidgetType.NUMBER, WidgetType.STATISTIC -> {
                WidgetCalculationResult(
                    title = widget.title,
                    type = widget.type,
                    mainValue = entries.size.toString()
                )
            }
            WidgetType.PROGRESS -> {
                val count = entries.size
                val percentage = (count.toDouble() / 100.0 * 100).coerceAtMost(100.0)
                WidgetCalculationResult(
                    title = widget.title,
                    type = widget.type,
                    mainValue = "${percentage.toInt()}%"
                )
            }
            WidgetType.TABLE, WidgetType.LIST -> {
                val listItems = entries.take(5).map { entry ->
                    val values = DataEngine.parseValues(entry.fieldValuesJson)
                    val label = values.values.firstOrNull() ?: "Entry"
                    val valStr = values.values.drop(1).firstOrNull() ?: ""
                    Pair(label, valStr)
                }
                WidgetCalculationResult(
                    title = widget.title,
                    type = widget.type,
                    mainValue = "${entries.size} entries",
                    items = listItems
                )
            }
            WidgetType.CHART -> {
                WidgetCalculationResult(
                    title = widget.title,
                    type = widget.type,
                    mainValue = "${entries.size} items in Chart"
                )
            }
        }
    }
}
