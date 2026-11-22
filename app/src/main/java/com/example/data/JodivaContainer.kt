package com.example.data

import android.content.Context
import com.example.data.ai.AiRepository
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.local.JodivaDatabase
import com.example.data.local.SessionPreferences
import com.example.data.repository.JodivaRepository

class JodivaContainer(context: Context) {
    private val database = JodivaDatabase.getInstance(context)
    private val preferences = SessionPreferences(context)
    private val aiRepository: AiRepository = PlaceholderAiRepository()

    val repository = JodivaRepository(
        dao = database.jodivaDao(),
        prefs = preferences,
        aiRepository = aiRepository
    )
}
