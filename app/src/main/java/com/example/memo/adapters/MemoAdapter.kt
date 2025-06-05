package com.example.memo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.R
import com.example.memo.models.Memo

class MemoAdapter(
    private var memoList: List<Memo>,
    private val onClick: (Memo) -> Unit,
    private val onLongClick: (Memo) -> Unit,
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.memoTitle)
        val content: TextView = itemView.findViewById(R.id.memoContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.title.text = memo.title
        holder.content.text = memo.content

        holder.itemView.setOnClickListener {
            val slideOut = TranslateAnimation(
                0f, holder.itemView.width.toFloat(), 0f, 0f
            ).apply {
                duration = 100
                fillAfter = true
            }
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    onClick(memo)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            holder.itemView.startAnimation(slideOut)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(memo)
            true
        }
    }

    fun updateData(newList: List<Memo>) {
        memoList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = memoList.size
}