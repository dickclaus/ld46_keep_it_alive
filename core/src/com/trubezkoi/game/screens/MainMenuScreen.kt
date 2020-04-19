package com.trubezkoi.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.trubezkoi.game.KeepMeAlive
import ktx.app.KtxScreen

class MainMenuScreen(
    private val keepMeAlive: KeepMeAlive
): KtxScreen {
    private val camera = OrthographicCamera().apply { setToOrtho(false, 800f, 480f) }

    override fun render(delta: Float) {
        camera.update()
        keepMeAlive.batch.projectionMatrix = camera.combined

        keepMeAlive.batch.begin()
        keepMeAlive.font.draw(keepMeAlive.batch, "Ludum Dare game by Dmitry Bezverkhiy LD46", 100f, 350f)
        keepMeAlive.font.draw(keepMeAlive.batch, "Hi, Hacker!", 100f, 150f)
        keepMeAlive.font.draw(keepMeAlive.batch, "I'm a computer virus and you should keep me alive.", 100f, 100f)
        keepMeAlive.batch.end()

        if (Gdx.input.isTouched) {
            keepMeAlive.addScreen(GameScreen(keepMeAlive))
            keepMeAlive.setScreen<GameScreen>()
            keepMeAlive.removeScreen<MainMenuScreen>()
            dispose()
        }
    }
}