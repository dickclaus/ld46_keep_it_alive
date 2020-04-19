package com.trubezkoi.game.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.trubezkoi.game.KeepMeAlive
import ktx.app.KtxScreen

class LoadingScreen(
    private val keepMeAlive: KeepMeAlive
): KtxScreen {
    private val camera = OrthographicCamera().apply { setToOrtho(false, 800f, 480f) }

    override fun render(delta: Float) {
        camera.update()
        if (keepMeAlive.manager.update()) {
            keepMeAlive.addScreen(MainMenuScreen(keepMeAlive))
            keepMeAlive.setScreen<MainMenuScreen>()
            keepMeAlive.removeScreen<LoadingScreen>()
            dispose()
        }

        keepMeAlive.batch.projectionMatrix = camera.combined

        keepMeAlive.batch.begin()
        keepMeAlive.font.draw(keepMeAlive.batch, "Loading ${Math.round(keepMeAlive.manager.progress * 100)}%", 10f, 460f)
        keepMeAlive.batch.end()

        super.render(delta)
    }
}