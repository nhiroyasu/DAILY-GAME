package com.cyan_namid09.dailygame.Home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyan_namid09.dailygame.DataBase.Rule
import com.cyan_namid09.dailygame.DataBase.RuleDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {
    val ruleList: MutableLiveData<List<Rule>> by lazy {
        MutableLiveData<List<Rule>>()
    }

    fun loadRuleList(context: Context) {
        viewModelScope.launch {
            val data = accessDB(context)
            ruleList.value = data
        }
    }

    private suspend fun accessDB(context: Context): List<Rule> = withContext(Dispatchers.IO) {
        val db = RuleDatabase.getDataBase(context).ruleDao()
        db.getAll()
    }
}