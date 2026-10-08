package com.example.singalanumbers

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var currentLanguage = "ru"
    private var isDarkTheme = false
    private var isLanguageSwitch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean("dark_theme", false)
        currentLanguage = prefs.getString("language", "ru") ?: "ru"

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.isHorizontalScrollBarEnabled = false
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                applyTheme()
                if (isLanguageSwitch) {
                    isLanguageSwitch = false
                    view?.post { view.clearHistory() }
                }
            }
        }
        
        webView.webChromeClient = WebChromeClient()
        
        val startPage = if (currentLanguage == "en") "menu_en.html" else "menu.html"
        webView.loadUrl("file:///android_asset/$startPage")

        val btnLanguage = findViewById<Button>(R.id.btnLanguage)
        val btnTheme = findViewById<Button>(R.id.btnTheme)
        val btnAbout = findViewById<Button>(R.id.btnAbout)

        btnLanguage.text = currentLanguage.uppercase()
        btnTheme.text = if (isDarkTheme) "☾" else "☀"
        
        val toolbar = findViewById<View>(R.id.toolbar_main)
        updateToolbarColors(toolbar, btnLanguage, btnTheme, btnAbout)

        btnLanguage.setOnClickListener {
            currentLanguage = if (currentLanguage == "ru") "en" else "ru"
            prefs.edit().putString("language", currentLanguage).apply()
            btnLanguage.text = currentLanguage.uppercase()
            
            val currentUrl = webView.url ?: ""
            val newUrl = if (currentLanguage == "en") {
                currentUrl.replace("menu.html", "menu_en.html")
                    .replace("index.html", "index_en.html")
                    .replace("letters.html", "letters_en.html")
                    .replace("advanced.html", "advanced_en.html")
            } else {
                currentUrl.replace("_en.html", ".html")
            }
            
            isLanguageSwitch = true
            webView.loadUrl(newUrl)
        }

        btnTheme.setOnClickListener {
            isDarkTheme = !isDarkTheme
            btnTheme.text = if (isDarkTheme) "☾" else "☀"
            prefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
            updateToolbarColors(toolbar, btnLanguage, btnTheme, btnAbout)
            applyTheme()
        }

        btnAbout.setOnClickListener {
            val aboutText = if (currentLanguage == "ru") {
                """
                <h3 style="color:#9c3b23; margin-bottom:8px;">සිංහල</h3>
                <p style="margin-bottom:12px;"><b>Сингальские числа и буквы</b></p>
                <p style="margin-bottom:10px;">Учебное приложение для запоминания сингальской письменности и числительных.</p>
                <p style="margin-bottom:10px;"><b>Числа:</b> 46 базовых карточек (от «эка» до «дахаса») и 20 сложных составных чисел. Колода с фильтрами, тренажёр и тест из 10 вопросов.</p>
                <p style="margin-bottom:10px;"><b>Буквы:</b> 47 букв алфавита, сгруппированных по варгам — горловые, нёбные, черепные, зубные, губные. Таблица с переворотом и тест в обе стороны.</p>
                <p style="margin-bottom:10px;"><b>Знаки гласных (пилли):</b> 14 знаков, которые крепятся к согласным и меняют их гласный звук.</p>
                <p style="margin-bottom:14px; font-style:italic; color:#7a7265;">В помощь изучающ.</p>
                <p style="margin-bottom:4px; color:#5a6b7a; font-size:0.9em;">Версия 1.0</p>
                <hr style="border:none; border-top:1px solid #d4c9b0; margin:12px 0;"/>
                <p style="font-size:0.9em;">По вопросам, связанным с приложением, обращайтесь:<br/><b style="color:#9c3b23;">asankhatabhikkhu@gmail.com</b></p>
                """.trimIndent()
            } else {
                """
                <h3 style="color:#9c3b23; margin-bottom:8px;">සිංහල</h3>
                <p style="margin-bottom:12px;"><b>Sinhala numbers and letters</b></p>
                <p style="margin-bottom:10px;">A study app for memorizing Sinhala script and numerals.</p>
                <p style="margin-bottom:10px;"><b>Numbers:</b> 46 basic cards (from "eka" to "dahasa") and 20 advanced composite numbers. Deck with filters, trainer with honest self-assessment, and a 10-question quiz.</p>
                <p style="margin-bottom:10px;"><b>Letters:</b> 47 letters grouped by vargas — gutturals, palatals, retroflexes, dentals, labials — as in Pali and Sanskrit. Table with flip and bidirectional test.</p>
                <p style="margin-bottom:10px;"><b>Vowel signs (pilli):</b> 14 signs attached to consonants that change their vowel sound.</p>
                <p style="margin-bottom:14px; font-style:italic; color:#7a7265;">Created with respect for the language and in support of practitioners.</p>
                <p style="margin-bottom:4px; color:#5a6b7a; font-size:0.9em;">Version 1.0</p>
                <hr style="border:none; border-top:1px solid #d4c9b0; margin:12px 0;"/>
                <p style="font-size:0.9em;">For questions related to the app, please contact:<br/><b style="color:#9c3b23;">asankhatabhikkhu@gmail.com</b></p>
                """.trimIndent()
            }

            val closeText = if (currentLanguage == "ru") "закрыть" else "close"
            AlertDialog.Builder(this)
                .setMessage(Html.fromHtml(aboutText, Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(closeText, null)
                .show()
        }
    }

    private fun updateToolbarColors(toolbar: View, btnLanguage: Button, btnTheme: Button, btnAbout: Button) {
        val bgColor = if (isDarkTheme) android.graphics.Color.parseColor("#3b3731") else android.graphics.Color.parseColor("#efe8da")
        val textColor = if (isDarkTheme) android.graphics.Color.parseColor("#efe8da") else android.graphics.Color.parseColor("#3b3733")
        toolbar.setBackgroundColor(bgColor)
        btnLanguage.setTextColor(textColor)
        btnTheme.setTextColor(textColor)
        btnAbout.setTextColor(textColor)
    }

    private fun applyTheme() {
        val theme = if (isDarkTheme) "dark" else "light"
        webView.evaluateJavascript("if(typeof setTheme === 'function') setTheme('$theme');", null)
    }

    override fun onBackPressed() {
        val currentUrl = webView.url ?: ""
        if (currentUrl.contains("menu.html") || currentUrl.contains("menu_en.html")) {
            super.onBackPressed()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}