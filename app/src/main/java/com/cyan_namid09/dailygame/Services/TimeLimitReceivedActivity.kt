package com.cyan_namid09.dailygame.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cyan_namid09.dailygame.*
import com.cyan_namid09.dailygame.DataBase.RuleDatabase
import com.cyan_namid09.dailygame.Notification.notifyRuleUpdate
import kotlinx.coroutines.*
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import kotlin.coroutines.CoroutineContext

class TimeLimitReceivedActivity: BroadcastReceiver(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val ruleId = intent.getIntExtra("rule_id", 0)
        launch {
            val title = getRuleTitle(context, ruleId)
            if (tweet(context, title)) {
                notifyRuleUpdate(context, title = title)
            }
        }
    }

    private suspend fun tweet(context: Context, title: String): Boolean = withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences(MAIN_SP, Context.MODE_PRIVATE)
        val cb = ConfigurationBuilder().apply {
            setDebugEnabled(true)
            setOAuthConsumerKey(OAUTH_KEY)
            setOAuthConsumerSecret(OAUTH_SECRET)
            setOAuthAccessToken(sharedPreferences.getString("token", ""))
            setOAuthAccessTokenSecret(sharedPreferences.getString("token_secret", ""))
        }

        val tf = TwitterFactory(cb.build())
        val twitter = tf.instance
        val sp = context.getSharedPreferences(MAIN_SP, Context.MODE_PRIVATE)
        val replaceName = sp.getString("penalty_name", TWITTER_NAME)
        val tweetText = sp.getString("penalty_tweet", TWEET_TEXT)?.replace("{rule}", title, false)
        try {
            twitter.updateProfile(replaceName, null, null, null)
            twitter.updateStatus(tweetText)
            true
        } catch (e: TwitterException) {
            Log.e("[TwitterException]", e.errorMessage)
            false
        }
    }

    private suspend fun getRuleTitle(context: Context, ruleId: Int): String = withContext(Dispatchers.IO) {
        RuleDatabase.getDataBase(context).ruleDao().loadById(ruleId = ruleId).first().name
    }
}