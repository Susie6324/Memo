import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.memo.models.Memo

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "users.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT
            )
        """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE memo(
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                username TEXT, 
                title TEXT, 
                content TEXT
            )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS memo")
        onCreate(db)
    }

    fun registerUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        addMemo(username, "欢迎使用备忘录", "记下内容")
        return db.insert("users", null, values) != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun addMemo(username: String, title: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("title", title)
            put("content", content)
        }
        return db.insert("memo", null, values)
    }

    fun updateMemo(id: Int, newTitle: String, newContent: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", newTitle)
            put("content", newContent)
        }
        return db.update(
            "memo",
            values,
            "id=?",
            arrayOf(id.toString())
        ) > 0
    }

    fun getMemosByUsername(username: String): List<Memo> {
        val db = readableDatabase

        val cursor = db.query(
            "memo", arrayOf("id", "title", "content"),
            "username=?", arrayOf(username),
            null, null, null
        )
        val memos = mutableListOf<Memo>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val content = cursor.getString(2)
            memos.add(Memo(id, title, content))
        }
        cursor.close()
        return memos
    }

    fun deleteMemoById(id: Int): Boolean {
        val db = writableDatabase
        return db.delete("memo", "id=?", arrayOf(id.toString())) > 0
    }
}