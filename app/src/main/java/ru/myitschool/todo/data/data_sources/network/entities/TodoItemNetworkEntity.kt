package ru.myitschool.todo.data.data_sources.network.entities

import com.google.gson.annotations.SerializedName


data class TodoItemNetworkEntity(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("done") val isDone: Boolean,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("changed_at") val changedAt: Long?,
    @SerializedName("color") val color: String = "#FFFFFF",
    @SerializedName("last_updated_by") val device: String = "this"
)