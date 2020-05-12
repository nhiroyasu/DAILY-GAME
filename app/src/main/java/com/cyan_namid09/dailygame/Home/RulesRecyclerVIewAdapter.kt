package com.cyan_namid09.dailygame.Home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cyan_namid09.dailygame.DataBase.Rule
import com.cyan_namid09.dailygame.R
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class RulesRecyclerVIewAdapter(val context: Context, val fragment: Fragment, val dataList: List<Rule>): RecyclerView.Adapter<RulesRecyclerVIewAdapter.ViewHolder>(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // contextはinflateするときに使う
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val card = view.card_on_rule_recycler_item
        val textView = view.title_on_rule_recycler_item
        val timeChip = view.time_limit_chip
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val limit = Calendar.getInstance().apply {
            time = dataList[position].time
        }
        val now = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        holder.textView.text = dataList[position].name
        holder.timeChip.text = when {
            limit < now -> {
                holder.timeChip.setTextColor(ContextCompat.getColor(context, R.color.colorDanger))
                holder.timeChip.setChipIconResource(R.drawable.ic_announcement_24px)
                holder.timeChip.setChipIconTintResource(R.color.colorDanger)
                String.format("%02d : %02d", limit.get(Calendar.HOUR_OF_DAY), limit.get(Calendar.MINUTE))
            }
            limit.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH) == 1 -> {
                holder.timeChip.setTextColor(ContextCompat.getColor(context, R.color.colorAppDark))
                holder.timeChip.setChipIconTintResource(R.color.colorAppDark)
                String.format("明日 %02d : %02d", limit.get(Calendar.HOUR_OF_DAY), limit.get(Calendar.MINUTE))
            }
            else -> {
                String.format("%02d : %02d", limit.get(Calendar.HOUR_OF_DAY), limit.get(Calendar.MINUTE))
            }
        }
        holder.card.setOnClickListener {
            it.transitionName = "shared_element_edit_container"
            holder.textView.transitionName = "shared_element_edit_title"
            holder.timeChip.transitionName = "shared_element_edit_time"
            val bundle = bundleOf(
                "ruleId" to dataList[position].id,
                "ruleTitle" to dataList[position].name,
                "ruleTime" to dataList[position].time.time)
            val extras = FragmentNavigatorExtras(
                it to "shared_element_edit_container")
            fragment.findNavController().navigate(R.id.action_homeFragment_to_editRuleFragment, bundle, null, extras)
        }
    }
}