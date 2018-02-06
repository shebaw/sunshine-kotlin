package com.example.shebaw.recyclerview2

import android.app.LauncherActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GreenAdapter(val numberOfItems: Int, val onClickListener: ListItemClickListener) :
        RecyclerView.Adapter<GreenAdapter.NumberViewHolder>() {
    interface ListItemClickListener {
        fun onListItemClick(index: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NumberViewHolder{
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.number_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        val viewHolder = NumberViewHolder(view)
        viewHolder.viewHolderIndex.text = "ViewHolder index: $viewHolderCount"
        val backgroundColorForViewHolder = ColorUtils.
                getViewHolderBackgroundColorFromInstance(context, viewHolderCount)
        viewHolder.itemView.setBackgroundColor(backgroundColorForViewHolder)

        viewHolderCount++
        Log.d("viewHolderCount", "$viewHolderCount")
        return viewHolder
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        Log.d(TAG, "#" + position)
        holder.bind(position)
    }

    override fun getItemCount() = numberOfItems

    // cache of the children view for a list item
    inner class NumberViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val listItemNumberView = itemView.findViewById(R.id.tv_item_number) as TextView
        val viewHolderIndex = itemView.findViewById(R.id.tv_view_holder_instance) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(listIndex: Int) {
            listItemNumberView.text = listIndex.toString()
        }

        override fun onClick(v: View?) {
            Log.d("onClick", "called for index $adapterPosition")
            onClickListener.onListItemClick(adapterPosition)
        }
    }

    companion object {
        private val TAG = GreenAdapter::class.java.simpleName
        private var viewHolderCount = 0
    }
}
