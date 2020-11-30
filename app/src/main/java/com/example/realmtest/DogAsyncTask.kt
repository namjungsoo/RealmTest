package com.example.realmtest

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import io.realm.Realm
import java.lang.ref.WeakReference

class DogAsyncTask() : AsyncTask<Dog, Void, Void>() {
    var realm: Realm? = null
    var contextRef: WeakReference<Context>? = null

    constructor(realm: Realm?, context: Context) : this() {
        this.realm = realm
        this.contextRef = WeakReference(context)
    }

    fun asyncWithMainThreadInstance(param: Dog?) {
        // main UI thread에서 받은 realm을 사용하면 안됨 IllegalStateException
        this.realm?.executeTransactionAsync(Realm.Transaction { realm ->
            realm.copyToRealm(param)
            },
            Realm.Transaction.OnSuccess {
            },
            Realm.Transaction.OnError {
                Log.e("JUNGSOO", it.localizedMessage)
            })
    }

    fun syncWithLocalInstance(param: Dog?) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            it.copyToRealm(param)
            it.close()
        }
    }

    fun asyncWithLocalInstance(param: Dog?) {
        // 여기서 새로 만들어야 함
        contextRef?.get()?.let {
            Log.e("JUNGSOO", "realm init with context in doInBackground")
//            Realm.init(it)
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync(Realm.Transaction { realm ->
                realm.copyToRealm(param)
            },
            Realm.Transaction.OnSuccess {
                realm.close()
            },
            Realm.Transaction.OnError {
                Log.e("JUNGSOO", it.localizedMessage)
                realm.close()
            })
        }

    }

    override fun doInBackground(vararg params: Dog?): Void? {
        val param = params[0]
//        asyncWithLocalInstance(param)
//        asyncWithMainThreadInstance(param)
        syncWithLocalInstance(param)
        return null
    }


}