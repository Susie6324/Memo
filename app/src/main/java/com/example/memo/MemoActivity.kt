package com.example.memo

import UserDatabaseHelper
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.adapters.MemoAdapter
import com.example.memo.models.Memo
import com.google.android.material.snackbar.Snackbar

class MemoActivity : ComponentActivity() {
    private lateinit var memoList: MutableList<Memo>
    private lateinit var adapter: MemoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        val username = intent.getStringExtra("username")
        val usernameText = findViewById<TextView>(R.id.textUsername)
        usernameText.text = "欢迎，$username"

        val recyclerView = findViewById<RecyclerView>(R.id.memoRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        memoList = getUserMemos(username ?: "").toMutableList()
        adapter = MemoAdapter(memoList, onClick = { memo ->
            val intent = Intent(this, EditMemoActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("id", memo.id)
            intent.putExtra("title", memo.title)
            intent.putExtra("content", memo.content)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }, onLongClick = { memo ->
            AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setMessage("确定要删除 \"${memo.title}\" 吗？")
                .setPositiveButton("删除") { _, _ ->
                    val index = memoList.indexOf(memo)
                    if (index != -1) {
                        val holder = recyclerView.findViewHolderForAdapterPosition(index)
                        val itemView = holder?.itemView
                        if (itemView != null) {
                            val slideOut = TranslateAnimation(
                                0f, -itemView.width.toFloat(), 0f, 0f
                            ).apply {
                                duration = 300
                                fillAfter = true
                            }
                            slideOut.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {
                                    val dbHelper = UserDatabaseHelper(this@MemoActivity)
                                    dbHelper.deleteMemoById(memo.id)
                                    memoList.removeAt(index)
                                    adapter.notifyItemRemoved(index)
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                            itemView.startAnimation(slideOut)
                        }
                    }
                }.setNegativeButton("取消", null).show()
        })
        recyclerView.adapter = adapter

        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val dbHelper = UserDatabaseHelper(this)
            val newId = dbHelper.addMemo(username.toString(), "新建备忘", "新内容").toInt()
            val newMemo = Memo(newId, "新建备忘", "新内容")
            memoList.add(newMemo)
            adapter.notifyItemInserted(memoList.size - 1)

            recyclerView.post {
                val holder = recyclerView.findViewHolderForAdapterPosition(memoList.size - 1)
                holder?.itemView?.startAnimation(
                    TranslateAnimation(
                        -holder.itemView.width.toFloat(), 0f, 0f, 0f
                    ).apply {
                        duration = 200
                    })
            }
        }

        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val searchInput = findViewById<EditText>(R.id.editSearch)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                val filteredList = memoList.filter {
                    it.title.contains(keyword, ignoreCase = true) ||
                            it.content.contains(keyword, ignoreCase = true)
                }

                adapter.updateData(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getUserMemos(username: String): List<Memo> {
        val dbHelper = UserDatabaseHelper(this)
        return dbHelper.getMemosByUsername(username)
    }
}