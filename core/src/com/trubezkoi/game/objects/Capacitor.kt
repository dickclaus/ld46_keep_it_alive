package com.trubezkoi.game.objects

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

class Capacitor {
    var currentPosition = Vector2(0f, 0f)
    var currentIndex = Vector2(0f, 0f)

    var nextPosition = Vector2(0f, 0f)
    var nextIndex = Vector2(0f, 0f)

    var sprite: Sprite? = null
    var isMoving = false

    fun update(delta: Float) {
        val distance = currentPosition.dst(nextPosition)
        if (isMoving && (abs(distance) > 0)) {
            println(distance)
            if (currentPosition.x != nextPosition.x) {
                if (currentPosition.x < nextPosition.x) {
                    if (currentPosition.x + delta >= nextPosition.x) {
                        currentPosition = Vector2(nextPosition.x, currentPosition.y)
                        currentIndex = Vector2(nextIndex.x, currentIndex.y)
                    } else {
                        currentPosition = Vector2(currentPosition.x + delta, currentPosition.y)
                    }
                } else if (currentPosition.x > nextPosition.x) {
                    if (currentPosition.x - delta <= nextPosition.x) {
                        currentPosition = Vector2(nextPosition.x, currentPosition.y)
                        currentIndex = Vector2(nextIndex.x, currentIndex.y)
                    } else {
                        currentPosition = Vector2(currentPosition.x - delta, currentPosition.y)
                    }
                }
            }
            if (currentPosition.y != nextPosition.y) {
                if (currentPosition.y < nextPosition.y) {
                    if (currentPosition.y + delta >= nextPosition.y) {
                        currentPosition = Vector2(currentPosition.x, nextPosition.y)
                        currentIndex = Vector2(currentIndex.x, nextIndex.y)
                    } else {
                        currentPosition = Vector2(currentPosition.x, currentPosition.y + delta)
                    }
                } else if (currentPosition.y > nextPosition.y) {
                    if (currentPosition.y - delta <= nextPosition.y) {
                        currentPosition = Vector2(currentPosition.x, nextPosition.y)
                        currentIndex = Vector2(currentIndex.x, nextIndex.y)
                    } else {
                        currentPosition = Vector2(currentPosition.x, currentPosition.y - delta)
                    }
                }
            }
        } else {
            isMoving = false
        }
    }
}