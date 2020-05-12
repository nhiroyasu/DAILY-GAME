package com.cyan_namid09.dailygame

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cyan_namid09.dailygame.DataBase.Rule
import com.cyan_namid09.dailygame.DataBase.RuleDatabase
import com.cyan_namid09.dailygame.Services.TimeLimitReceivedActivity
import com.cyan_namid09.dailygame.databinding.FragmentCreateRuleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class CreateRuleFragment : Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding:FragmentCreateRuleBinding
    private var selectTime: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateRuleBinding.inflate(inflater, container, false)
        binding.createRuleTimeButton.setOnClickListener {
            val startCal = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 30)
            }
            TimePickerDialog(
                context!!,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    this.selectTime = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    val now = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                    }
                    binding.selectedTimeLabel.text = String.format("%02d : %02d", hourOfDay, minute)
                    if (this.selectTime!! <= now) {
                        MaterialAlertDialogBuilder(context!!).apply {
                            setTitle("指定時間外")
                            setMessage("過去の時間を選択しています。\nこの場合、期限は明日になります。")
                            setPositiveButton("OK") { dialog, which ->

                            }
                            setIcon(R.drawable.ic_icon_24px)
                        }.show()
                    }
                },
                startCal.get(Calendar.HOUR_OF_DAY),
                startCal.get(Calendar.MINUTE),
                true).show()
        }
        binding.createRuleButton.setOnClickListener {
            if (binding.createRuleTitle.text.toString() == "" || this.selectTime == null) {
                warningAlertNonParam()
            } else {
                launch {
                    val time = selectTime
                    if (time != null) {
                        val now = Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis()
                        }
                        if (time <= now) time.apply { add(Calendar.DAY_OF_MONTH, 1) }
                        val rowId = insertRuleToDB(name = binding.createRuleTitle.text.toString(), cal = time)
                        setAlarmManager(id = rowId.toInt(), timeLimit = time)
                        findNavController().navigateUp()
                    }
                }
            }
        }

        return binding.root
    }

    private fun setAlarmManager(id: Int, timeLimit: Calendar) {
        val intent = Intent(context!!.applicationContext, TimeLimitReceivedActivity::class.java)
        intent.putExtra("rule_id", id)
        val sender = PendingIntent.getBroadcast(context!!, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.RTC_WAKEUP, timeLimit.timeInMillis, sender)
    }

    private suspend fun insertRuleToDB(name: String, cal: Calendar): Long = withContext(Dispatchers.IO) {
        val rule = Rule(id = 0,name = name, time = cal.time)
        RuleDatabase.getDataBase(context!!).ruleDao().insert(rule)
    }

    private fun warningAlertNonParam() {
        MaterialAlertDialogBuilder(context!!).apply {
            setTitle("エラー")
            setMessage("入力していない値があります")
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

            })
        }.show()
    }
}
