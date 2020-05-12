package com.cyan_namid09.dailygame

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cyan_namid09.dailygame.DataBase.RuleDatabase
import com.cyan_namid09.dailygame.Services.TimeLimitReceivedActivity
import com.cyan_namid09.dailygame.databinding.FragmentEditRuleBinding
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_ID = "ruleId"
private const val ARG_TITLE = "ruleTitle"
private const val ARG_TIME = "ruleTime"

/**
 * A simple [Fragment] subclass.
 * Use the [EditRuleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditRuleFragment : Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    // TODO: Rename and change types of parameters
    private var id: Int? = null
    private var title: String? = null
    private var time: Long? = null

    private lateinit var binding: FragmentEditRuleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_ID)
            title = it.getString(ARG_TITLE)
            time = it.getLong(ARG_TIME)
        }
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            // オブション
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditRuleBinding.inflate(inflater, container, false)
        binding.titleOnEditRule.text = this.title ?: "Null"
        binding.timeOnEditRule.also {
            val tmpTime = Calendar.getInstance().also {
                it.timeInMillis = this.time ?: 0L
            }
            it.text = String.format("%02d : %02d", tmpTime.get(Calendar.HOUR_OF_DAY), tmpTime.get(Calendar.MINUTE))
        }
        binding.doneButtonOnEditRule.setOnClickListener {
            val id = this.id
            launch {
                if (id != null) {
                    deleteRuleOnDB(id)
                    deleteAlarmProcess(id)
                }
                fragment.findNavController().navigateUp()
            }
        }
        return binding.root
    }

    private suspend fun deleteRuleOnDB(id: Int) = withContext(Dispatchers.IO) {
        RuleDatabase.getDataBase(context!!).ruleDao().delete(id)
    }

    private fun deleteAlarmProcess(id: Int) {
        val intent = Intent(context!!.applicationContext, TimeLimitReceivedActivity::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context!!, id, intent, PendingIntent.FLAG_NO_CREATE) ?: return
        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent.cancel()
        am.cancel(pendingIntent)
    }
}
