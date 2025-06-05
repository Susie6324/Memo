package com.example.memo

import UserDatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper = UserDatabaseHelper(this)

        val loginPanel = findViewById<FrameLayout>(R.id.loginPanel)
        val loginContent = findViewById<View>(R.id.loginContent)
        loginContent.visibility = View.INVISIBLE
        loginPanel.visibility = View.VISIBLE
        loginPanel.translationX = 1000f
        loginPanel.animate()
            .translationX(0f)
            .setDuration(1000)
            .withEndAction {
                val fadeIn = AlphaAnimation(0f, 1f).apply {
                    duration = 600
                    fillAfter = true
                }
                loginContent.startAnimation(fadeIn)
                loginContent.visibility = View.VISIBLE
            }
            .start()

        val editUsername = findViewById<EditText>(R.id.Username)
        val editPassword = findViewById<EditText>(R.id.Password)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonShow = findViewById<Button>(R.id.buttonShow)

        buttonLogin.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.checkUser(username, password)) {
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                    loginPanel.animate()
                        .translationX(-1000f)
                        .setDuration(800)
                        .withEndAction {
                            loginPanel.visibility = View.GONE
                            val intent = Intent(this, MemoActivity::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }
                        .start()
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonRegister.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.registerUser(username, password)) {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "已存在用户，注册失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

        var passwordShow = false
        buttonShow.setOnClickListener {
            passwordShow = !passwordShow
            if (passwordShow) {
                editPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            editPassword.setSelection(editPassword.text.length)
        }
    }
}