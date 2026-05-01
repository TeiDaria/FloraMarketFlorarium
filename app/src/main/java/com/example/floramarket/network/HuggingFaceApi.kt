package com.example.floramarket.network

import retrofit2.Call
import retrofit2.http.*

interface HuggingFaceApi {

    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun generateName(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String = "deepseek-ai/DeepSeek-V3",
    val messages: List<ChatMessage>,
    val max_tokens: Int = 50,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String,  // "user" или "system"
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessage
)