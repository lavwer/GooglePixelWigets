package com.widgetcraft.app.data

/** Один стиль оформления виджета: цвета циферблата, стрелок и текста. */
data class WidgetTemplate(
    val id: String,
    val name: String,
    val showSeconds: Boolean = true,
    val dialColorFrom: Int,
    val dialColorTo: Int,
    val ringColor: Int,
    val tickMajorColor: Int,
    val tickMinorColor: Int,
    val hourHandColor: Int,
    val minuteHandColor: Int,
    val secondHandColor: Int,
    val digitalTextColor: Int,
    val scrimColor: Int
)
