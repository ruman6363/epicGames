package com.example.egsapp

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.egsapp.adapter.CurrentRecyclerAdapter
import com.example.egsapp.adapter.FutureRecyclerAdapter
import com.example.egsapp.database.DbManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener

class MainActivity : AppCompatActivity() {
    /*
    Идея приложения:
    После установки и первого запуска загружается пустая активити, в которой есть предложение подгрузить данные
    Сверху справа есть кнопка с закрученной стрелкой - это кнопка для создания запроса к api
    После того как нажимаешь на нее - получаешь данные, чистишь бд и заливаешь свежие данные
    При последующих запусках приложение тянет из бд уже полученные данные
    Так решается частично проблема 10 запросов в месяц
    В любой момент на страх и риск можно обновить данные, но, если ошибка, то появится Тост в ошибкой (но данные из бд не удалятся
    [будут от последнего удачного запроса])
    */

    //Объявляем переменные для элементов
    private lateinit var statusText: TextView
    private lateinit var currentText: TextView
    private lateinit var futureText: TextView

    //Объявляем листы для игр
    private val gameList = ArrayList<Game>()
    private val gameFutList = ArrayList<Game>()

    private val dbManager = DbManager(this) //Инициализация бд-менеджера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init() //Метод инициализации элементов на активити

        readData() //Метод считывания данных из БД

        setStatus() //Установка статусов на активити
        setCurrentAdapter(gameList) //Загрузка адаптера текущих игр
        setFutureAdapter(gameFutList) //Загрузка адаптера будущих игр
    }

    private fun insertData() { //Метод записи в бд
        try {
            val thread = Thread { //Готовим отдельный поток для работы с api
                //Работа с api
                val client = OkHttpClient()
                val request = Request.Builder() //Собираем через билдер запрос
                    .url("https://free-epic-games.p.rapidapi.com/free")
                    .get()
                    .addHeader("X-RapidAPI-Key", "531597c006msh7b2eedadf7a590bp10af15jsn3ba9d7400d03")
                    .addHeader("X-RapidAPI-Host", "free-epic-games.p.rapidapi.com")
                    .build()

                val response = client.newCall(request).execute() //Отправка запроса в api
                val result = response.body()?.string() //Получение результатов в видо json файла
                var error = JSONObject(result).optString("message") //Опционально отслеживаем поле message в полученном json файле
                if (error.isNotEmpty()) { //Если поле message существует и не пустое, то
                    runOnUiThread { //Возврат в основной поток
                        statusText.text = "Конец света! С API что-то не то!" //Заполнение поля статуса сообщением с ошибкой
                        Toast.makeText(this, "Конец света! С API что-то не то!", Toast.LENGTH_SHORT).show() //Вывод Тоста с ошибкой
                    }
                } else { //Если все нормально и api выдала результат
                    //Разбор JSON файлика
                    val jsonObject = JSONTokener(result).nextValue() as JSONObject //Определяем объект
                    val jsonFreeGames = jsonObject.getJSONObject("freeGames") //Спускаемся на уровень freeGames
                    val jsonArray = jsonFreeGames.getJSONArray("current") //Извлекаем массив JSON с текущими играми
                    val jsonArrayFuture = jsonFreeGames.getJSONArray("upcoming") //Извлекаем массив JSON с будущими играми
                    runOnUiThread { //Возврат в основной поток
                        dbManager.openDb() //Открываем бд
                        dbManager.deleteFromDb() //Очищаем старые данные
                        for (i in 0 until jsonArray.length()) { //Перебираем все объекты из json массива
                            //Заполняем поля заголовка, описания, тащим ссылку на картинку
                            val title = jsonArray.getJSONObject(i).getString("title")
                            val desc = jsonArray.getJSONObject(i).getString("description")
                            //Так как картинка в отдельном массиве в массиве, то парсим подмассив и тянем ссылку из нулевого индекса
                            val image = jsonArray.getJSONObject(i).getJSONArray("keyImages").getJSONObject(0).getString("url")
                            dbManager.insertToDb(Game(title, desc, image, "current")) //Записываем в бд объект data class'a Game с меткой current
                        }
                        for (i in 0 until jsonArrayFuture.length()) { //Аналогичные действия с будущими играми (добавляем метку future)
                            val title = jsonArrayFuture.getJSONObject(i).getString("title")
                            val desc = jsonArrayFuture.getJSONObject(i).getString("description")
                            val image = jsonArrayFuture.getJSONObject(i).getJSONArray("keyImages").getJSONObject(0).getString("url")
                            dbManager.insertToDb(Game(title, desc, image, "future"))
                        }
                        dbManager.closeDb() //Закрываем бд
                    }
                }
            }
            thread.start() //Открытие потока
        }
        catch (ex: Exception){
            println(ex)
        }
    }

    private fun init(){ //Метод привязки переменных к элементам на активити
        statusText = findViewById(R.id.statusText)
        futureText = findViewById(R.id.futureText)
        currentText = findViewById(R.id.currentText)
    }

    private fun setStatus() { //Меняем видимость текстовых объектов
        if (gameList.isEmpty()){ //Если не получили нормальный json из api
            statusText.visibility = View.VISIBLE
            currentText.visibility = View.INVISIBLE
            futureText.visibility = View.INVISIBLE
        }
        else{ //Если получили нормальный файл
            statusText.visibility = View.INVISIBLE
            currentText.visibility = View.VISIBLE
            futureText.visibility = View.VISIBLE
        }
    }

    private fun readData(){ //Метод считывания из бд
        try {
            dbManager.openDb() //Открываем бд
            val tempData = dbManager.readDbDataFromDb() //Заводим временный лист и льем в него все данные из бд
            for (i in 0 .. tempData.size){ //Перебор временного листа
                if (tempData[i].status.equals("current")){ //Если в поле статуса current, то
                    gameList.add(tempData[i]) //Пишем в gameList
                }
                else{
                    gameFutList.add(tempData[i]) //В ином случае в gameFutList
                }
            }
            dbManager.closeDb() //Закрываем бд
        }
        catch (ex: Exception){
            println(ex)
        }

    }


    private fun setCurrentAdapter(game: ArrayList<Game>){ //Адаптер текущих игр
        val recyclerView: RecyclerView = findViewById(R.id.currentView) //Подвязка ресайклера к объекту
        val linearLayoutManager = LinearLayoutManager(applicationContext) //Подготовка лайаут менеджера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager //Инициализация лайаут менеджера
        recyclerView.adapter = CurrentRecyclerAdapter(game!!) //внесение данных из листа в адаптер (заполнение данными)
    }

    private fun setFutureAdapter(gameFut: ArrayList<Game>){ //Адаптер будущих игр
        val recyclerView: RecyclerView = findViewById(R.id.futureView) //Подвязка ресайклера к объекту
        val linearLayoutManager = LinearLayoutManager(applicationContext) //Подготовка лайаут менеджера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager //Инициализация лайаут менеджера
        recyclerView.adapter = FutureRecyclerAdapter(gameFut!!) //внесение данных из листа в адаптер (заполнение данными)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //Подключаем меню к action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) { //Слушаем нажатие на кнопку по id
        R.id.action_refresh -> {
            Toast.makeText(this, "Обновляю данные...", Toast.LENGTH_SHORT).show()
            insertData() //Считываем данные с api

            //Используем задержку перед обновлением адаптеров и считыванием данных (задержка нужна для того, чтобы адекватно распарсить данные и залить их в бд)
            val handler = Handler()
            handler.postDelayed(Runnable {
                readData()

                setCurrentAdapter(gameList)
                setFutureAdapter(gameFutList)
            }, 5000) //Задержка в 5 секунд

            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}