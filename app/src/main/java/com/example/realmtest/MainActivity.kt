package com.example.realmtest

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

class MainActivity : AppCompatActivity() {
    var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dog = Dog()
        dog.age = 1
        dog.name = "Rex"

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        val task = DogAsyncTask(realm)
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dog)
    }
}