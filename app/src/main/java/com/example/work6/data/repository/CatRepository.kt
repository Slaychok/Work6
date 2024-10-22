package com.example.work6.data.repository

import com.example.work6.data.database.CatDao
import com.example.work6.data.model.Cat
import com.example.work6.data.network.CatApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CatRepository @Inject constructor(
    private val catDao: CatDao, private val catApi: CatApi
) {

    fun fetchCatFromApi(callback: (Result<List<Cat>>) -> Unit) {
        catApi.getCat().enqueue(object : Callback<List<Cat>> {
            override fun onResponse(call: Call<List<Cat>>, response: Response<List<Cat>>) {
                if (response.isSuccessful) {
                    response.body()?.let { cats ->
                        callback(Result.success(cats))
                    } ?: run {
                        callback(Result.failure(Throwable("No cat data found")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error code: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    suspend fun saveCatToDb(cat: Cat) = withContext(Dispatchers.IO) {
        catDao.insertCat(cat)
    }

    suspend fun getCatFromDb(): Cat? = withContext(Dispatchers.IO) {
        catDao.getCat()
    }
}