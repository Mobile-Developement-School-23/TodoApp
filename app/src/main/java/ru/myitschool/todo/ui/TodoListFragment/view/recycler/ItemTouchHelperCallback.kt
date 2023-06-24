package ru.myitschool.todo.ui.TodoListFragment.view.recycler

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.todo.R
import kotlin.math.abs
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
        if (abs(dX) > view.width / 1.2) {
            return
        }
        if (dX > 0) {
            val paint = Paint()
            paint.color = Color.parseColor("#00FF00")
            c.drawRect(view.left.toFloat(), view.top.toFloat(), dX, view.bottom.toFloat(), paint)
            val drawable = ResourcesCompat.getDrawable(
                view.resources,
                R.drawable.ic_check, view.context.theme
            )
            drawable?.setBounds(
                min(view.left+view.height / 3, (view.left + dX).toInt()),
                view.top + view.height / 3,
                view.left+view.height / 3 * 2,
                view.bottom - view.height / 3
            )
            drawable?.draw(c)
        } else if (dX < 0) {
            val paint = Paint()
            paint.color = Color.parseColor("#FF0000")
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
                view.right - view.height / 3,
                view.bottom - view.height / 3
            )
            drawable?.draw(c)
        }
//        drawable?.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}