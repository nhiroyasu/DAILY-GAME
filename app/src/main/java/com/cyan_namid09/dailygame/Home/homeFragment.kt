package com.cyan_namid09.dailygame.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cyan_namid09.dailygame.LoginActivity
import com.cyan_namid09.dailygame.R
import com.cyan_namid09.dailygame.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import kotlin.coroutines.CoroutineContext

class homeFragment() : Fragment(), CoroutineScope {
    private lateinit var binding: FragmentHomeBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var model: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ctx = context ?: return inflater.inflate(R.layout.fragment_home, container, false)

        model.ruleList.observe(this.viewLifecycleOwner, Observer { newValue ->
            val adapter =
                RulesRecyclerVIewAdapter(
                    ctx,
                    this,
                    newValue
                )
            binding.rulesRecyclerView.adapter = adapter
        })

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setting -> {
                    findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
                    true
                }
                R.id.help -> {
                    findNavController().navigate(R.id.action_homeFragment_to_descriptionFragment)
                    true
                }
                else -> {
                    false
                }

            }
        }
        binding.addRulesButton.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                binding.addRulesButton to "shared_element_create_container")
            findNavController().navigate(R.id.action_homeFragment_to_createRuleFragment, null, null, extras)
//            findNavController().navigate(R.id.action_homeFragment_to_createRuleFragment)
        }
        binding.rulesRecyclerView.isNestedScrollingEnabled = false
        binding.rulesRecyclerView.layoutManager = GridLayoutManager(ctx, 1)

        // load model
        model.loadRuleList(ctx)
        return binding.root
    }
}
