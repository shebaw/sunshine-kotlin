package com.example.shebaw.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 *
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 *
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 *
 * If you don't like our puns, we named this Adapter GreenAdapter because its
 * contents are green.
 */
class GreenAdapter
/**
 * Constructor for GreenAdapter that accepts a number of items to display and the specification
 * for the ListItemClickListener.
 *
 * @param numberOfItems Number of items to display in list
 */
(
        val count: Int) : RecyclerView.Adapter<GreenAdapter.NumberViewHolder>() {

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    override fun getItemCount() = count

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     * can use this viewType integer to provide a different layout. See
     * [android.support.v7.widget.RecyclerView.Adapter.getItemViewType]
     * for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NumberViewHolder {
        val context = viewGroup.getContext()
        val layoutIdForListItem = R.layout.number_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)

        return NumberViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        Log.d(TAG, "#" + position)
        holder.bind(position)
    }

    /**
     * Cache of the children views for a list item.
     */
    inner class NumberViewHolder
    /**
     * Constructor for our ViewHolder. Within this constructor, we get a reference to our
     * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
     * onClick method below.
     * @param itemView The View that you inflated in
     * [GreenAdapter.onCreateViewHolder]
     */
    (itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        val listItemNumberView = itemView.findViewById(R.id.tv_item_number) as TextView

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        fun bind(listIndex: Int) {
            listItemNumberView.setText(listIndex.toString())
        }
    }

    companion object {

        private val TAG = GreenAdapter::class.java.getSimpleName()
    }
}
