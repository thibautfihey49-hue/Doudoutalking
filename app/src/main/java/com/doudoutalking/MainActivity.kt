package com.doudoutalking

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var doudouImage: ImageView
    private lateinit var btnTalk: ImageView
    private lateinit var btnFeed: ImageView
    private lateinit var btnSleep: ImageView
    private lateinit var btnPlay: ImageView
    private lateinit var btnPet: ImageView
    private lateinit var btnDrink: ImageView
    private lateinit var statusHunger: ProgressBar
    private lateinit var statusHappy: ProgressBar
    private lateinit var statusEnergy: ProgressBar
    private lateinit var textCoins: TextView
    private lateinit var textMessage: TextView

    private lateinit var tts: TextToSpeech
    private var isSleeping = false
    private var coins = 100
    private var hunger = 80
    private var happiness = 70
    private var energy = 90
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var animationBounce: Animation
    private lateinit var animationShake: Animation
    private lateinit var animationSleep: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initAnimations()
        updateUI()

        // Diminution automatique des statistiques
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isSleeping) {
                    hunger = (hunger - 1).coerceAtLeast(0)
                    happiness = (happiness - 1).coerceAtLeast(0)
                    energy = (energy - 1).coerceAtLeast(0)
                } else {
                    energy = (energy + 2).coerceAtMost(100)
                    hunger = (hunger - 1).coerceAtLeast(0)
                }
                updateUI()
                handler.postDelayed(this, 3000)
            }
        }, 3000)

        // 🐻 Cliquer sur le doudou
        doudouImage.setOnClickListener {
            if (isSleeping) {
                wakeUp()
            } else {
                petDoudou()
            }
        }

        // 🗣️ Parler au doudou
        btnTalk.setOnClickListener {
            if (!isSleeping) {
                speakAndReact("Tu me parles ! Je suis tout ouïe !")
                earnCoins(1)
            }
        }

        // 🍖 Nourrir
        btnFeed.setOnClickListener {
            if (!isSleeping && hunger < 100) {
                feedDoudou()
            }
        }

        // 💤 Dormir
        btnSleep.setOnClickListener {
            if (isSleeping) wakeUp() else sleep()
        }

        // 🎮 Jouer
        btnPlay.setOnClickListener {
            if (!isSleeping) playGame()
        }

        // 🤔 Caresser
        btnPet.setOnClickListener {
            if (!isSleeping) petDoudou()
        }

        // 💧 Boire
        btnDrink.setOnClickListener {
            if (!isSleeping) drink()
        }
    }

    private fun initViews() {
        doudouImage = findViewById(R.id.doudou_image)
        btnTalk = findViewById(R.id.btn_talk)
        btnFeed = findViewById(R.id.btn_feed)
        btnSleep = findViewById(R.id.btn_sleep)
        btnPlay = findViewById(R.id.btn_play)
        btnPet = findViewById(R.id.btn_pet)
        btnDrink = findViewById(R.id.btn_drink)
        statusHunger = findViewById(R.id.status_hunger)
        statusHappy = findViewById(R.id.status_happy)
        statusEnergy = findViewById(R.id.status_energy)
        textCoins = findViewById(R.id.text_coins)
        textMessage = findViewById(R.id.text_message)
        tts = TextToSpeech(this, this)
    }

    private fun initAnimations() {
        animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        animationShake = AnimationUtils.loadAnimation(this, R.anim.shake)
        animationSleep = AnimationUtils.loadAnimation(this, R.anim.sleep)
    }

    private fun updateUI() {
        statusHunger.progress = hunger
        statusHappy.progress = happiness
        statusEnergy.progress = energy
        textCoins.text = "💰 $coins"

        // Changement d'expression selon l'humeur
        when {
            isSleeping -> doudouImage.setImageResource(R.drawable.ic_doudou_sleep)
            hunger < 20 -> {
                doudouImage.setImageResource(R.drawable.ic_doudou_sad)
                showMessage("J'ai faim ! 🍖")
            }
            happiness < 20 -> {
                doudouImage.setImageResource(R.drawable.ic_doudou_sad)
                showMessage("Je m'ennuie... 😢")
            }
            energy < 20 -> {
                doudouImage.setImageResource(R.drawable.ic_doudou_tired)
                showMessage("Je suis fatigué... 💤")
            }
            else -> doudouImage.setImageResource(R.drawable.ic_doudou_happy)
        }
    }

    private fun showMessage(msg: String) {
        textMessage.text = msg
        handler.postDelayed({ textMessage.text = "" }, 3000)
    }

    private fun earnCoins(amount: Int) {
        coins += amount
        textCoins.startAnimation(animationBounce)
        updateUI()
    }

    private fun speakAndReact(text: String) {
        doudouImage.startAnimation(animationBounce)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun petDoudou() {
        val reactions = listOf(
            "Ah ! C'est agréable ! 😊",
            "Tu es mon meilleur ami ! ❤️",
            "Encore ! Encore ! 🥰",
            "Je suis tout doux ! 🐻",
            "Tu me rends heureux ! 😄"
        )
        speakAndReact(reactions.random())
        happiness = (happiness + 5).coerceAtMost(100)
        earnCoins(2)
        updateUI()
    }

    private fun feedDoudou() {
        speakAndReact("Miam miam ! C'était délicieux ! 🍖")
        doudouImage.startAnimation(animationBounce)
        hunger = (hunger + 25).coerceAtMost(100)
        happiness = (happiness + 3).coerceAtMost(100)
        earnCoins(3)
        updateUI()
    }

    private fun drink() {
        speakAndReact("Glou glou ! Merci pour l'eau ! 💧")
        doudouImage.startAnimation(animationBounce)
        energy = (energy + 10).coerceAtMost(100)
        earnCoins(2)
        updateUI()
    }

    private fun sleep() {
        isSleeping = true
        doudouImage.startAnimation(animationSleep)
        speakAndReact("Bonne nuit... Fais de beaux rêves 💤")
        updateUI()
    }

    private fun wakeUp() {
        isSleeping = false
        doudouImage.startAnimation(animationBounce)
        speakAndReact("Bonjour ! Je suis en forme ! ☀️")
        updateUI()
    }

    private fun playGame() {
        val games = listOf("slot", "dice", "love")
        when (games.random()) {
            "slot" -> {
                val win = Random.nextBoolean()
                if (win) {
                    speakAndReact("JACKPOT ! 🎉 Tu as gagné 10 pièces !")
                    earnCoins(10)
                } else {
                    speakAndReact("Presque ! Retente ta chance ! 🎰")
                    earnCoins(1)
                }
            }
            "dice" -> {
                val de = Random.nextInt(1, 7)
                speakAndReact("J'ai fait un $de ! 🎲")
                earnCoins(de)
            }
            "love" -> {
                speakAndReact("Je t'aime de tout mon coeur ! ❤️❤️❤️")
                happiness = 100
                earnCoins(5)
            }
        }
        updateUI()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.FRENCH
            speakAndReact("Coucou ! Je suis ton Doudou Gris ! 🐻")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        tts.stop()
        tts.shutdown()
    }
}
