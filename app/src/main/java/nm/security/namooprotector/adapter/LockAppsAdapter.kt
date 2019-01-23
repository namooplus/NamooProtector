package nm.security.namooprotector.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import nm.security.namooprotector.R
import nm.security.namooprotector.element.AppElement

class LockAppsAdapter(private val context: Context, private val element: ArrayList<AppElement>, val itemClick: (AppElement) -> Unit) : RecyclerView.Adapter<LockAppsAdapter.Holder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        return Holder(LayoutInflater.from(context).inflate(R.layout.list_lock_apps, parent, false))
    }
    override fun onBindViewHolder(holder: Holder, position: Int)
    {
        holder.bind(element[position])
    }

    //메소드
    override fun getItemCount(): Int
    {
        return element.size
    }

    //홀더
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val icon = itemView.findViewById<ImageView>(R.id.list_lock_apps_icon)
        val label = itemView.findViewById<TextView>(R.id.list_lock_apps_label)
        val packageName = itemView.findViewById<TextView>(R.id.list_lock_apps_package_name)
        val state = itemView.findViewById<ImageView>(R.id.list_lock_apps_state)

        fun bind (appElement: AppElement)
        {
            icon.setImageDrawable(appElement.icon)
            label.text = appElement.label
            packageName.text = appElement.packageName
            state.visibility = if(appElement.state) View.VISIBLE else View.INVISIBLE

            itemView.setOnClickListener { itemClick(appElement) }
        }
    }
}