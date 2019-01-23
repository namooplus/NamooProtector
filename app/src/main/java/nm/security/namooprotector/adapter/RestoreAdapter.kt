package nm.security.namooprotector.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import nm.security.namooprotector.R
import nm.security.namooprotector.element.BackupElement

class RestoreAdapter(private val context: Context, private val element: ArrayList<BackupElement>, val itemClick: (BackupElement) -> Unit) : RecyclerView.Adapter<RestoreAdapter.Holder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        return Holder(LayoutInflater.from(context).inflate(R.layout.list_restore, parent, false))
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
        val label = itemView.findViewById<TextView>(R.id.list_restore_label)
        val date = itemView.findViewById<TextView>(R.id.list_restore_date)

        fun bind (restoreElement: BackupElement)
        {
            label.text = restoreElement.label
            date.text = restoreElement.date

            itemView.setOnClickListener { itemClick(restoreElement) }
        }
    }
}