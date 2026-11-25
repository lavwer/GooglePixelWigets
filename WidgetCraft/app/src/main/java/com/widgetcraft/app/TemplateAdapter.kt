package com.widgetcraft.app

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.widgetcraft.app.data.WidgetTemplate
import com.widgetcraft.app.widgets.ClockRenderer

/** Сетка шаблонов: мини-предпросмотр циферблата для каждого стиля. */
class TemplateAdapter(
    private val templates: List<WidgetTemplate>,
    private val onClick: (WidgetTemplate) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.Holder>() {

    private var selectedId: String? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val preview: ImageView = view.findViewById(R.id.preview)
        val name: TextView = view.findViewById(R.id.name)
        val card: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_template, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = templates.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val template = templates[position]
        holder.name.text = template.name
        holder.preview.setImageDrawable(
            BitmapDrawable(holder.preview.resources, ClockRenderer.renderDial(template))
        )
        val selected = template.id == selectedId
        holder.card.alpha = if (selectedId == null || selected) 1f else 0.45f
        holder.card.setOnClickListener {
            selectedId = template.id
            notifyDataSetChanged()
            onClick(template)
        }
    }

    fun selectedTemplate(): WidgetTemplate? =
        templates.firstOrNull { it.id == selectedId }
}
