package com.example.egsapp

data class Game( //Дата класс с полями: Заголовок, Описание, ссылка на изображение, Статус игры
    val title: String?,
    val description: String?,
    val imageURL: String?,
    val status: String?
)
