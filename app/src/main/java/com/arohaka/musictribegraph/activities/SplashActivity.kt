package com.arohaka.musictribegraph.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arohaka.musictribegraph.MainActivity
import com.arohaka.musictribegraph.R
import com.arohaka.musictribegraph.databinding.ActivitySplashBinding
import com.robinhood.ticker.TickerUtils

class SplashActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        mBinding.tvMusic.setCharacterLists(TickerUtils.provideAlphabeticalList())
        mBinding.tvMusic.animationInterpolator = OvershootInterpolator()
        mBinding.tvMusic.text = "Tribe"

        mBinding.lavMusic.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                mBinding.tvMusic.text = "Music Tribe"
            }

            override fun onAnimationEnd(p0: Animator?) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })

    }
}