package nm.security.namooprotector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nm.security.namooprotector.R
import nm.security.namooprotector.bundle.AppBundle

class AppsAdapter(private val context: Context, private val list: ArrayList<AppBundle>, val itemClick: (AppBundle) -> Unit) : RecyclerView.Adapter<AppsAdapter.Holder>(), Filterable
{
    var filteredList = list

    //라이프 사이클
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        return Holder(LayoutInflater.from(context).inflate(R.layout.view_app_bundle, parent, false))
    }
    override fun onBindViewHolder(holder: Holder, position: Int)
    {
        holder.bind(filteredList[position])
    }

    //메소드
    override fun getItemCount(): Int
    {
        return filteredList.size
    }
    override fun getFilter(): Filter
    {
        return object : Filter()
        {
            var searchingList  = ArrayList<AppBundle>()

            override fun performFiltering(keyword: CharSequence): FilterResults
            {
                if (keyword.toString().isEmpty()) searchingList = list

                else
                {
                    for (appBundle in list)
                    {
                        if (appBundle.label.toLowerCase().contains(keyword.toString().toLowerCase()))
                            searchingList.add(appBundle)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = searchingList

                return filterResults
            }
            override fun publishResults(keyword: CharSequence, results: FilterResults)
            {
                filteredList = results.values as ArrayList<AppBundle>
                notifyDataSetChanged()
            }
        }
    }

    //클래스
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val icon = itemView.findViewById<ImageView>(R.id.view_app_bundle_icon)
        private val label = itemView.findViewById<TextView>(R.id.view_app_bundle_label)
        private val packageName = itemView.findViewById<TextView>(R.id.view_app_bundle_package_name)
        private val state = itemView.findViewById<ImageView>(R.id.view_app_bundle_state)

        fun bind (bundle: AppBundle)
        {
            icon.setImageDrawable(bundle.icon)
            label.text = bundle.label
            packageName.text = bundle.packageName
            state.visibility = if(bundle.state) View.VISIBLE else View.INVISIBLE

            itemView.setOnClickListener { itemClick(bundle) }
        }
    }
}