package com.doudoutalking

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var doudouImage: ImageView
    private lateinit var inputText: EditText
    private lateinit var btnParler: Button
    private lateinit var btnToucher: Button
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doudouImage = findViewById(R.id.doudou_image)
        inputText = findViewById(R.id.input_text)
        btnParler = findViewById(R.id.btn_parler)
        btnToucher = findViewById(R.id.btn_toucher)

        textToSpeech = TextToSpeech(this, this)

        btnParler.setOnClickListener {
            val texte = inputText.text.toString().trim()
            if (texte.isNotEmpty()) {
                parler(texte)
                inputText.text.clear()
            }
        }

        btnToucher.setOnClickListener {
            parler("Tu me touches ! Je suis un doudou tout doux !")
        }

        doudouImage.setOnClickListener {
            parler("Coucou ! Je suis ton doudou gris !")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) textToSpeech.language = Locale.FRENCH
    }

    private fun parler(texte: String) {
        textToSpeech.speak(texte, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
