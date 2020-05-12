package com.cyan_namid09.dailygame

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.cyan_namid09.dailygame.DataBase.RuleDatabase
import com.cyan_namid09.dailygame.Services.TimeLimitReceivedActivity
import com.cyan_namid09.dailygame.databinding.FragmentSettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SettingFragment : Fragment(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        val ctx = context
        if (ctx != null) {
            val sharedPreference = ctx.getSharedPreferences(MAIN_SP, Context.MODE_PRIVATE)
            binding.textOfAccountId.text = String.format("@%s", sharedPreference.getString("twitter_id", "null"))
            binding.textFieldOfTwitterName.apply {
                val default = sharedPreference.getString("penalty_name", TWITTER_NAME)
                setText(default, TextView.BufferType.NORMAL)
            }
            binding.textFieldOfTweet.apply {
                val default = sharedPreference.getString("penalty_tweet", TWEET_TEXT)
                setText(default, TextView.BufferType.NORMAL)
            }
            binding.saveButtonOfTwitter.setOnClickListener {
                val name = binding.textFieldOfTwitterName.text.toString()
                val tweet = binding.textFieldOfTweet.text.toString()
                sharedPreference.edit().putString("penalty_name", name).apply()
                sharedPreference.edit().putString("penalty_tweet", tweet).apply()
                Toast.makeText(ctx, "保存しました", Toast.LENGTH_SHORT).show()
            }
            binding.signOutButton.setOnClickListener {
                MaterialAlertDialogBuilder(ctx).apply {
                    setTitle("サインアウト")
                    setMessage("サインアウトすると今まで作成したルールが全て削除されます")
                    setIcon(R.drawable.ic_icon_24px)
                    setPositiveButton("OK") { dialog, which ->
                        launch {
                            signOutProcess()
                            findNavController().navigateUp()
                            val intent = Intent(ctx, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    setNegativeButton("キャンセル") { dialog, which ->
                    }
                }.show()
            }
            binding.homeBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private suspend fun signOutProcess() {
        withContext(Dispatchers.IO) {
            val dao = RuleDatabase.getDataBase(context!!).ruleDao()
            dao.getAll().forEach {
                deleteAlarmProcess(id = it.id)
                dao.delete(ruleId = it.id)
            }
        }
        FirebaseAuth.getInstance().signOut()
    }

    private fun deleteAlarmProcess(id: Int) {
        val intent = Intent(context!!.applicationContext, TimeLimitReceivedActivity::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context!!, id, intent, PendingIntent.FLAG_NO_CREATE) ?: return
        val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent.cancel()
        am.cancel(pendingIntent)
    }
}
