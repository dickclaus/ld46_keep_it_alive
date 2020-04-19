package com.trubezkoi.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.trubezkoi.game.KeepMeAlive
import ktx.app.KtxScreen

class GameOverScreen(
    private val keepMeAlive: KeepMeAlive
): KtxScreen {
    private val camera = OrthographicCamera().apply { setToOrtho(false, 800f, 480f) }

    override fun render(delta: Float) {
        camera.update()
        keepMeAlive.batch.projectionMatrix = camera.combined

        keepMeAlive.batch.begin()
        keepMeAlive.font.color = Color.WHITE
        keepMeAlive.font.draw(keepMeAlive.batch, "Thanks, Hacker!", 100f, 150f)
        keepMeAlive.font.draw(keepMeAlive.batch, "You did a great job, thank you for playing", 100f, 100f)
        keepMeAlive.batch.end()

        if (Gdx.input.isTouched) {
            keepMeAlive.removeScreen<GameOverScreen>()
            dispose()
            Gdx.app.exit()
        }
    }
}