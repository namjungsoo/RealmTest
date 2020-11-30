package com.example.realmtest

import android.content.Context
import android.os.AsyncTask
import android.os.Looper
import android.util.Log
import io.realm.Realm
import java.lang.ref.WeakReference

class DogAsyncTask() : AsyncTask<Dog, Void, Void>() {
    var realm: Realm? = null

    constructor(realm: Realm?) : this() {
        this.realm = realm
    }

    // 동작안함
    // 다른 쓰레드에서 생성한 instance 사용
    fun asyncWithMainThreadInstance(param: Dog?) {
        // UI 쓰레드에서 받은 realm을 다른 쓰레드에서 사용하면 안됨 IllegalStateException
        this.realm?.executeTransactionAsync(Realm.Transaction { realm ->
            realm.copyToRealm(param)
            },
            Realm.Transaction.OnSuccess {
            },
            Realm.Transaction.OnError {
                Log.e("JUNGSOO", it.localizedMessage)
            })
    }

    // 정상동작
    // instance 생성한 쓰레드에서 execute
    fun syncWithLocalInstance(param: Dog?) {
        val localRealm = Realm.getDefaultInstance()
        localRealm.executeTransaction {
            it.copyToRealm(param)
            //it.close() // 여기서 닫으면 crash
        }
        localRealm.close() // 정상적으로 닫힌다
    }

    // 동작안함
    // 새로 생성했지만 looper가 없음
    fun asyncWithLocalInstance(param: Dog?) {
        // 여기서 새로 만들어야 함
        Log.e("JUNGSOO", "realm init with context in doInBackground")
        val realm = Realm.getDefaultInstance()

        // 이것도 역시 IllegalStateException 발생
        // AsyncTask는 Looper가 없으므로 발생함
        //java.lang.IllegalStateException: Callback cannot be delivered on current thread. Realm cannot be automatically updated on a thread without a looper.
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

    override fun doInBackground(vararg params: Dog?): Void? {
        val param = params[0]
//        asyncWithLocalInstance(param)
//        asyncWithMainThreadInstance(param)
        syncWithLocalInstance(param)
        return null
    }


}