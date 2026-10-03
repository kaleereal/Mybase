package com.mybase.app.domain.view

import com.mybase.app.domain.model.ViewType
import kotlinx.serialization.Serializable

@Serializable
data class ViewConfig(
    val filterGroupJson: String = "",
    val sortRulesJson: String = "",
    val visibleFieldIds: List<String> = emptyList(),
    val groupFieldId: String? = null,
    val coverImageFieldId: String? = null,
    val kanbanStatusFieldId: String? = null,
    val calendarDateFieldId: String? = null,
    val timelineStartFieldId: String? = null,
    val timelineEndFieldId: String? = null,
    val mapLocationFieldId: String? = null
)
