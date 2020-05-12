package com.cyan_namid09.dailygame

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.navigation.fragment.findNavController
import com.cyan_namid09.dailygame.databinding.FragmentDescriptionBinding

/**
 * A simple [Fragment] subclass.
 * Use the [DescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DescriptionFragment : Fragment() {

    private lateinit var binding: FragmentDescriptionBinding
    private val UsageText = """
        使い方は至ってシンプルです！
        TODOアプリのようにタスク（このアプリではルールと呼びます）を追加してください。
        そのルールを期間中に完了できなければ、そのことをツイートしちゃいます！
        ついでにツイッターの表示名も変わってしまいます。
        恥をかきたくたくないのなら、しっかりルールを完了させましょう！
        ちなみに、ツイート文や表示名は設定から自由に変更できます。
    """.trimIndent()
    private val TwoGateDevCampLink = "<a href=\"https://devcamp2020.twogate.com/\">TwoGate Dev Camp</a>"
    private val TeamMember1Link = "<a href=\"https://twitter.com/FPC_COMMUNITY\">@FPC_COMMUNITY</a>"
    private val TeamMember2Link = "<a href=\"https://twitter.com/Chaha1n\">@Chaha1n</a>"
    private val TeamMember3Link = "<a href=\"https://twitter.com/cyan_Programing\">@cyan_Programing</a>"
    private val AboutAppText = """
            このアプリは ${TwoGateDevCampLink} で考案、開発されたWebアプリ<br>「DailyGame」<br>をそのままスマホアプリ化したものです。<br>
            - 共に開発してくれたチームメンバーのみなさん ↓<br>
            ${TeamMember1Link}<br>
            ${TeamMember2Link}<br>
            ${TeamMember3Link} (私)<br>
            ありがとうございます！
        """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        binding.usageText.text = UsageText
        binding.aboutAppText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(AboutAppText, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(AboutAppText)
        }
        binding.aboutAppText.movementMethod = LinkMovementMethod.getInstance()
        binding.homeBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}
