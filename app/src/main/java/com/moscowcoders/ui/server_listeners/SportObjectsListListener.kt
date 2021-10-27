package com.moscowcoders.ui.server_listeners

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.moscowcoders.data.models.sport_objects.SportObjectModel

// Слушатель изменений в списке спортивных объектов в базе данных

class SportObjectsListListener : ValueEventListener {

    // Тег для логов
    private val TAG_VALUEEVENTLISTENER = "MySportObjectsListListener"
    private var list = mutableListOf<SportObjectModel>()
    val liveData: MutableLiveData<MutableList<SportObjectModel>> = MutableLiveData()

    override fun onDataChange(snapshot: DataSnapshot) {

        //Log.d(TAG_VALUEEVENTLISTENER, "Ответ сразу после приёма: ${snapshot}")

        list.clear()

        for (oneSnapshot: DataSnapshot in snapshot.children) {
            val newSportObject: SportObjectModel? =
                oneSnapshot.getValue(SportObjectModel::class.java)

            val isOpen = oneSnapshot.child("isOpen").value

            if (newSportObject != null) {
                newSportObject.isOpen = isOpen as Boolean
                list.add(newSportObject)
            }

            //Log.d(TAG_VALUEEVENTLISTENER, "Ответ: ${oneSnapshot}")

            //Log.d(TAG_VALUEEVENTLISTENER, "Мой объект: ${newSportObject}")
        }

        val listOfNames = mutableListOf<String>()
        for(i in list){
            i.name?.let { listOfNames.add(it) }

            val timeHelper = i.days?.let { TimeHelper(it) }
            if (timeHelper != null) {
                timeHelper.sortMap()
            }
        }

        Log.d(TAG_VALUEEVENTLISTENER, "Список перед обновлением livedata: ${listOfNames}")

        liveData.value = list
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d(TAG_VALUEEVENTLISTENER, "Код ошибки: ${error.code}. Ошибка: ${error.message}")
    }
}