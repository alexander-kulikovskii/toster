package fi.epicbot.toster.samples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SampleParamsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_params)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = "${textView.text} ${getDataFromIntent(intent)}"
    }

    private fun getDataFromIntent(intent: Intent): Int {
        return if (intent.hasExtra(INT_KEY)) {
            intent.getIntExtra(INT_KEY, DEFAULT_INT_KEY)
        } else {
            DEFAULT_INT_KEY
        }
    }

    private companion object {
        private const val INT_KEY = "int_value"
        private const val DEFAULT_INT_KEY = 0
    }
}
