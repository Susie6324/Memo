package com.example.memo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.R
import com.example.memo.models.Memo

class MemoAdapter(
    private val memoList: List<Memo>,
    private val onClick: (Memo) -> Unit,
    private val onLongClick: (Memo) -> Unit,
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.memoTitle)
        val content: TextView = itemView.findViewById(R.id.memoContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.title.text = memo.title
        holder.content.text = memo.content

        holder.itemView.setOnClickListener {
            onClick(memo)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(memo)
            true
        }
    }

    override fun getItemCount(): Int = memoList.size
}