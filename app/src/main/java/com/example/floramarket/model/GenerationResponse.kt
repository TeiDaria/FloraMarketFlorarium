package com.example.floramarket.model

import com.google.gson.annotations.SerializedName

// Hugging Face API возвращает список результатов
data class GenerationResponse(
    @SerializedName("generated_text")
    val generatedText: String
)