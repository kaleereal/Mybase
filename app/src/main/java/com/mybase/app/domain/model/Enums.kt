package com.mybase.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FieldType {
    TEXT,
    LONG_TEXT,
    NUMBER,
    DECIMAL,
    CURRENCY,
    PERCENTAGE,
    RATING,
    CHECKBOX,
    CHOICE,
    MULTIPLE_CHOICE,
    DATE,
    TIME,
    DATE_TIME,
    DURATION,
    IMAGE,
    FILE,
    AUDIO,
    VIDEO,
    URL,
    EMAIL,
    PHONE,
    LOCATION,
    COLOR,
    BARCODE,
    QR_CODE,
    RELATION,
    MULTIPLE_RELATION,
    FORMULA,
    CALCULATION,
    JAVASCRIPT,
    AUTO_NUMBER,
    CREATED_TIME,
    MODIFIED_TIME
}

@Serializable
enum class RelationType {
    ONE_TO_ONE,
    ONE_TO_MANY,
    MANY_TO_MANY
}

@Serializable
enum class ViewType {
    LIST,
    TABLE,
    GRID,
    GALLERY,
    KANBAN,
    CALENDAR,
    TIMELINE,
    MAP
}

@Serializable
enum class FilterOperator {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    NOT_CONTAINS,
    GREATER_THAN,
    LESS_THAN,
    BETWEEN,
    IS_EMPTY,
    IS_NOT_EMPTY
}

@Serializable
enum class LogicalOperator {
    AND,
    OR
}

@Serializable
enum class TriggerType {
    ENTRY_CREATED,
    ENTRY_UPDATED,
    ENTRY_DELETED,
    FIELD_CHANGED
}

@Serializable
enum class ActionType {
    UPDATE_FIELD,
    CREATE_ENTRY,
    EVALUATE_FORMULA,
    EXECUTE_SCRIPT
}

@Serializable
enum class WidgetType {
    NUMBER,
    STATISTIC,
    TABLE,
    LIST,
    CHART,
    PROGRESS
}

@Serializable
enum class ChartType {
    BAR,
    LINE,
    PIE,
    DONUT,
    AREA,
    SCATTER
}
