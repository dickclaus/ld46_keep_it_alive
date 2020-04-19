package com.trubezkoi.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.trubezkoi.game.screens.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.load
import ktx.assets.setLoader
import ktx.inject.Context

class KeepMeAlive: KtxGame<KtxScreen>() {
    private val context = Context()
    val manager = AssetManager()
    var currentLevel = 1
    var applesEaten = 0

    val batch by lazy { SpriteBatch() }
    // use LibGDX's default Arial font
    val font by lazy { BitmapFont() }

    override fun create() {
        manager.setLoader(TmxMapLoader())
        manager.load<TiledMap>("lev01.tmx")
        manager.load<TiledMap>("lev02.tmx")
        manager.load<TiledMap>("lev03.tmx")
        manager.load<Texture>("virus.png")
        manager.load<Texture>("capacitor.png")
        manager.load<Sound>("eat.wav")
        manager.load<Sound>("saw.wav")
        manager.load<Sound>("eatApple.wav")
        manager.load<Music>("ld46music.mp3")

        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
//        addScreen(GameScreen(this))
//        setScreen<GameScreen>()
        super.create()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}