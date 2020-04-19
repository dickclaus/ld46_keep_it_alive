package com.trubezkoi.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.trubezkoi.game.KeepMeAlive

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            title = "Keep me alive"
            width = 800
            height = 480
        }
        LwjglApplication(KeepMeAlive(), config).logLevel = Application.LOG_DEBUG
    }
}