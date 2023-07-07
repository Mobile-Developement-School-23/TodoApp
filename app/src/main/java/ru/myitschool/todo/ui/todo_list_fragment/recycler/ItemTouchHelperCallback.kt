package ru.myitschool.todo.ui.todo_list_fragment.recycler

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.todo.R
import kotlin.math.max
import kotlin.math.min

class ItemTouchHelperCallback(
    private val adapter: ItemTouchHelperAdapter
) : ItemTouchHelper.Callback() {
    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START or ItemTouchHelper.END)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START) {
            adapter.onItemDismiss(viewHolder.adapterPosition)
        } else {
            adapter.onItemChecked(viewHolder.adapterPosition)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        adapter.onItemSelected(actionState)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val view = viewHolder.itemView
        if (dX > 0) {
            val paint = Paint()
            paint.color = view.resources.getColor(R.color.green, view.context.theme)
            c.drawRect(view.left.toFloat(), view.top.toFloat(), dX, view.bottom.toFloat(), paint)
            val drawable = ResourcesCompat.getDrawable(
                view.resources,
                R.drawable.ic_check, view.context.theme
            )
            drawable?.setBounds(
                min(view.left+view.height / 3, (view.left+dX-view.height / 3).toInt()),
                view.top + view.height / 3,
                min(view.left+view.height / 3 * 2, (view.left+dX).toInt()),
                view.bottom - view.height / 3
            )
            drawable?.draw(c)
        } else if (dX < 0) {
            val paint = Paint()
            paint.color = view.resources.getColor(R.color.red, view.context.theme)
            c.drawRect(
                view.right + dX, view.top.toFloat(),
                view.right.toFloat(), view.bottom.toFloat(), paint
            )
            val drawable = ResourcesCompat.getDrawable(
                view.resources,
                R.drawable.ic_delete, view.context.theme
            )
            drawable?.setBounds(
                max(view.right - view.height / 3 * 2, (view.right + dX).toInt()),
                view.top + view.height / 3,
                max(view.right - view.height / 3, (view.right+dX+view.height / 3).toInt()),
                view.bottom - view.height / 3
            )
            drawable?.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
interface ItemTouchHelperAdapter {
    fun onItemDismiss(position:Int)
    fun onItemChecked(position: Int)
    fun onItemSelected(actionState:Int)
}