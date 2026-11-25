package com.widgetcraft.app.data

import android.graphics.Color

/** Каталог встроенных шаблонов оформления. */
object TemplateStore {

    val all: List<WidgetTemplate> = listOf(
        WidgetTemplate(
            id = "neon",
            name = "Неон",
            dialColorFrom = Color.parseColor("#11141B"),
            dialColorTo = Color.parseColor("#232A3A"),
            ringColor = Color.parseColor("#00E5FF"),
            tickMajorColor = Color.parseColor("#00E5FF"),
            tickMinorColor = Color.parseColor("#3A4456"),
            hourHandColor = Color.parseColor("#FFFFFF"),
            minuteHandColor = Color.parseColor("#FFFFFF"),
            secondHandColor = Color.parseColor("#FF3D9A"),
            digitalTextColor = Color.parseColor("#00E5FF"),
            scrimColor = Color.parseColor("#99001018")
        ),
        WidgetTemplate(
            id = "gold",
            name = "Золото",
            dialColorFrom = Color.parseColor("#17130A"),
            dialColorTo = Color.parseColor("#2E2410"),
            ringColor = Color.parseColor("#D4AF37"),
            tickMajorColor = Color.parseColor("#E8C86A"),
            tickMinorColor = Color.parseColor("#5A4B26"),
            hourHandColor = Color.parseColor("#F5E3A8"),
            minuteHandColor = Color.parseColor("#F5E3A8"),
            secondHandColor = Color.parseColor("#B8860B"),
            digitalTextColor = Color.parseColor("#E8C86A"),
            scrimColor = Color.parseColor("#99120E04")
        ),
        WidgetTemplate(
            id = "minimal",
            name = "Минимализм",
            showSeconds = false,
            dialColorFrom = Color.parseColor("#FAFAFA"),
            dialColorTo = Color.parseColor("#E8E8E8"),
            ringColor = Color.parseColor("#111111"),
            tickMajorColor = Color.parseColor("#111111"),
            tickMinorColor = Color.parseColor("#CCCCCC"),
            hourHandColor = Color.parseColor("#111111"),
            minuteHandColor = Color.parseColor("#111111"),
            secondHandColor = Color.parseColor("#111111"),
            digitalTextColor = Color.parseColor("#111111"),
            scrimColor = Color.parseColor("#99FFFFFF")
        ),
        WidgetTemplate(
            id = "retro",
            name = "Ретро",
            showSeconds = false,
            dialColorFrom = Color.parseColor("#2B2118"),
            dialColorTo = Color.parseColor("#4A3A28"),
            ringColor = Color.parseColor("#C8A97E"),
            tickMajorColor = Color.parseColor("#E8D5B5"),
            tickMinorColor = Color.parseColor("#6B5940"),
            hourHandColor = Color.parseColor("#F0E2C8"),
            minuteHandColor = Color.parseColor("#F0E2C8"),
            secondHandColor = Color.parseColor("#B08968"),
            digitalTextColor = Color.parseColor("#E8D5B5"),
            scrimColor = Color.parseColor("#99140F06")
        ),
        WidgetTemplate(
            id = "ocean",
            name = "Океан",
            dialColorFrom = Color.parseColor("#06222E"),
            dialColorTo = Color.parseColor("#0B3D4E"),
            ringColor = Color.parseColor("#2EC4B6"),
            tickMajorColor = Color.parseColor("#7FDBDA"),
            tickMinorColor = Color.parseColor("#17505C"),
            hourHandColor = Color.parseColor("#FFFFFF"),
            minuteHandColor = Color.parseColor("#FFFFFF"),
            secondHandColor = Color.parseColor("#FF9F1C"),
            digitalTextColor = Color.parseColor("#7FDBDA"),
            scrimColor = Color.parseColor("#99042028")
        ),
        WidgetTemplate(
            id = "sunset",
            name = "Закат",
            dialColorFrom = Color.parseColor("#2D1B2E"),
            dialColorTo = Color.parseColor("#5C2A3E"),
            ringColor = Color.parseColor("#FF6B6B"),
            tickMajorColor = Color.parseColor("#FFB4A2"),
            tickMinorColor = Color.parseColor("#6E3B4A"),
            hourHandColor = Color.parseColor("#FFE5D9"),
            minuteHandColor = Color.parseColor("#FFE5D9"),
            secondHandColor = Color.parseColor("#FF8FA3"),
            digitalTextColor = Color.parseColor("#FFB4A2"),
            scrimColor = Color.parseColor("#99200F1E")
        ),
        WidgetTemplate(
            id = "forest",
            name = "Лес",
            dialColorFrom = Color.parseColor("#0F2417"),
            dialColorTo = Color.parseColor("#1E3D29"),
            ringColor = Color.parseColor("#81B29A"),
            tickMajorColor = Color.parseColor("#B7D1A8"),
            tickMinorColor = Color.parseColor("#2A4A36"),
            hourHandColor = Color.parseColor("#F4F1DE"),
            minuteHandColor = Color.parseColor("#F4F1DE"),
            secondHandColor = Color.parseColor("#E07A5F"),
            digitalTextColor = Color.parseColor("#B7D1A8"),
            scrimColor = Color.parseColor("#990A1E12")
        ),
        WidgetTemplate(
            id = "graphite",
            name = "Графит",
            showSeconds = false,
            dialColorFrom = Color.parseColor("#1A1A1C"),
            dialColorTo = Color.parseColor("#333336"),
            ringColor = Color.parseColor("#8A8A8E"),
            tickMajorColor = Color.parseColor("#D1D1D6"),
            tickMinorColor = Color.parseColor("#48484A"),
            hourHandColor = Color.parseColor("#F2F2F7"),
            minuteHandColor = Color.parseColor("#F2F2F7"),
            secondHandColor = Color.parseColor("#98989D"),
            digitalTextColor = Color.parseColor("#D1D1D6"),
            scrimColor = Color.parseColor("#99111114")
        )
    )

    val default: WidgetTemplate = all.first()

    fun byId(id: String?): WidgetTemplate = all.firstOrNull { it.id == id } ?: default
}
