package fi.epicbot.toster.samples

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Locale

class SampleLanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale()
        setContentView(R.layout.activity_language)
    }

    private fun setLocale() {
        val config = resources.configuration
        val lang = getDataFromIntent(intent)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale)
        else
            config.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getDataFromIntent(intent: Intent): String {
        return if (intent.hasExtra(LOCALE_KEY)) {
            intent.getStringExtra(LOCALE_KEY)!!
        } else {
            DEFAULT_LOCALE
        }
    }

    private companion object {
        private const val LOCALE_KEY = "locale"
        private const val DEFAULT_LOCALE = "en"
    }
}
