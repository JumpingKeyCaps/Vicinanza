package com.lebaillyapp.vicinanza

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
            delay(2000)
            isReady = true
        }
        // 4. L'animation de sortie (le zoom)
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
            val scaleX = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_X, 1f, 2f)
            val scaleY = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_Y, 1f, 2f)
            AnimatorSet().apply {
                duration = 500L
                playTogether(alpha, scaleX, scaleY)
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }
        // 5. Le reste de l' UI
        enableEdgeToEdge()
        setContent {
            VicinanzaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(text = "Hello !", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
