package com.bignerdranch.android.pr17_pavlov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val curr = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (curr == null) {
            val fragment = FragmentCrime()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}



data class Crime(val id: UUID = UUID.randomUUID()) {
    var title: String = ""
    var isSolved: Boolean = false
    var suspect: String = ""
    val date: Date = Date()
}

