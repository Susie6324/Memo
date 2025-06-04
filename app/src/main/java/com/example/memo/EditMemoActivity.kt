package com.example.memo

import UserDatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class EditMemoActivity : ComponentActivity() {

    private lateinit var username: String
    private var originalTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)

        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        username = intent.getStringExtra("username") ?: ""
        val id = intent.getIntExtra("id", -1)
        originalTitle = intent.getStringExtra("title")
        val existingContent = intent.getStringExtra("content")

        if (originalTitle != null) {
            editTitle.setText(originalTitle)
            editContent.setText(existingContent)
        }

        buttonSave.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val content = editContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbHelper = UserDatabaseHelper(this)
            val success = dbHelper.updateMemo(id, title, content)

            if (success) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MemoActivity::class.java)
                intent.putExtra("username", username)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}