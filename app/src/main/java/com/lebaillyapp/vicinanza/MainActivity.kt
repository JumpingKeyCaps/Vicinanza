package com.lebaillyapp.vicinanza

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.lebaillyapp.vicinanza.ui.theme.VicinanzaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. On initialise l'instance
        super.onCreate(savedInstanceState)
        // 2. On installe le splash
        val splashScreen = installSplashScreen()
        // 3. La condition de maintien ( timer de 2s) // TEST ONLY !
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch {
            delay(800)
            isReady = true
        }
        // 4. L'animation de sortie (le zoom)
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            triggerDoubleTapHaptic() // micro vivration prenium
            // 1. On sépare l'icône du fond (View)
            val iconView = splashScreenView.iconView
            val backgroundView = splashScreenView.view // Le fond noir

            // 2. L'icône s'envole (Anticipate)
            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 2f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 2f)
            val iconAlpha = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)

            // et il disparaît plus lentement pour couvrir l'UI
            val backAlpha = ObjectAnimator.ofFloat(backgroundView, View.ALPHA, 1f, 0f).apply {
                duration = 800L
                startDelay = 400L
            }

            AnimatorSet().apply {
                duration = 1000L
                interpolator = AnticipateInterpolator()
                playTogether(scaleX, scaleY, iconAlpha, backAlpha)
                doOnEnd { splashScreenView.remove() }
                start()
            }


        }
        // 5. Le reste de l' UI
        enableEdgeToEdge()
        setContent {
            VicinanzaTheme {
                // On anime l'entrée de toute l'UI
                val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(Unit) {
                    // On attend un peu que le splash commence à s'envoler
                    delay(500)
                    alphaAnim.animateTo(1f, animationSpec = tween(600))
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize().graphicsLayer(alpha = alphaAnim.value)
                ) { innerPadding ->
                    Text(text = "Hello !", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }


    private fun triggerDoubleTapHaptic() {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator

        // Pattern : [Attente, Vibre, Attente, Vibre]
        // Intensités : [0, 150, 0, 150] (sur 255)
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 15, 60, 15),
            intArrayOf(0, 120, 0, 120),
            -1
        )
        vibrator.vibrate(effect)
    }

}





