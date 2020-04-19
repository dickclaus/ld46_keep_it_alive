package com.trubezkoi.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.trubezkoi.game.KeepMeAlive
import com.trubezkoi.game.objects.Capacitor
import com.trubezkoi.game.objects.MapObject
import ktx.app.KtxScreen
import ktx.tiled.layer
import kotlin.math.abs

const val SPRITE_SIZE: Float = 32f

class GameScreen(private val keepMeAlive: KeepMeAlive): KtxScreen {
    private val camera = OrthographicCamera().apply { setToOrtho(false, 800f, 480f) }
    private val player by lazy { Player() }
    private var capacitors = mutableListOf<Capacitor>()
    private var texture: Texture? = null
    private var sprite: Sprite? = null
    private var map: TiledMap? = null
    private var soundId: Long = 0L

    override fun show() {
        map = keepMeAlive.manager.get<TiledMap>("lev0${keepMeAlive.currentLevel}.tmx")
        val music = keepMeAlive.manager.get<Music>("ld46music.mp3")
        music.volume = 0.5f
        music.isLooping = true
        music.play()
        texture = keepMeAlive.manager.get<Texture>("virus.png")
        sprite = Sprite(texture, 0, 0, SPRITE_SIZE.toInt(), SPRITE_SIZE.toInt())

        capacitors = createCapacitors()

        val playerStartPosition = getPlayerStartPosition()
        player.currentIndexX = playerStartPosition.x.toInt()
        player.currentIndexY = playerStartPosition.y.toInt()
        player.currentX = playerStartPosition.x * SPRITE_SIZE
        player.currentY = playerStartPosition.y * SPRITE_SIZE
        player.capacitors = capacitors
        player.map = map
        super.show()
    }

    private fun getPlayerStartPosition(): Vector2 {
        val layer: TiledMapTileLayer = map!!.layer("walls") as TiledMapTileLayer
        for (i in 0 until layer.width) {
            for (j in 0 until layer.height) {
                val cell = layer.getCell(i, j)
                if (cell != null && cell.tile.id == MapObject.START.tileId) {
                    return Vector2(i.toFloat(), j.toFloat())
                }
            }
        }
        return Vector2(0f, 0f)
    }

    private fun createCapacitors(): MutableList<Capacitor> {
        var capacitors = mutableListOf<Capacitor>()
        val capacitorTexture = keepMeAlive.manager.get<Texture>("capacitor.png")
        val capacitorSprite = Sprite(capacitorTexture, 0,0,32,32)
        val layer: TiledMapTileLayer = map!!.layer("objs") as TiledMapTileLayer
        for (i in 0 until layer.width) {
            for (j in 0 until layer.height) {
                val cell = layer.getCell(i, j)
                if (cell != null && cell.tile.id == MapObject.CAPACITOR.tileId) {
                    val capacitor = Capacitor()
                    capacitor.sprite = capacitorSprite
                    capacitor.currentIndex = Vector2(i.toFloat(), j.toFloat())
                    capacitor.currentPosition = Vector2(i * SPRITE_SIZE, j * SPRITE_SIZE)
                    capacitors.add(capacitor)
                }
            }
        }
        return capacitors

    }

    override fun dispose() {
        super.dispose()
    }

    override fun render(delta: Float) {
        camera.update()

        val tiledMapRenderer = OrthogonalTiledMapRenderer(map, keepMeAlive.batch)
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render(arrayOf(0).toIntArray())

        checkCapacitorFalls()
        renderAll(delta)
        updatePlayer(delta)
        updateCapacitors(delta)
        updateMap()

    }

    private fun renderAll(delta: Float) {
        keepMeAlive.batch.projectionMatrix = camera.combined
        keepMeAlive.batch.begin()
        keepMeAlive.font.color = Color.BLACK
        keepMeAlive.font.draw(keepMeAlive.batch, "Apples eaten: ${keepMeAlive.applesEaten}", 34f, 32f)
        keepMeAlive.batch.draw(sprite, player.currentX , player.currentY)
        for (capacitor in capacitors) {
            keepMeAlive.batch.draw(capacitor.sprite, capacitor.currentPosition.x, capacitor.currentPosition.y)
        }
        keepMeAlive.batch.end()
    }

    private fun checkCapacitorFalls() {
        val layer: TiledMapTileLayer = map!!.layer("lev01") as TiledMapTileLayer
        for (capacitor in capacitors) {
            if (capacitor.isMoving) continue
            val indexBelow = Vector2(capacitor.currentIndex.x, capacitor.currentIndex.y - 1f)
            if (
                layer.getCell(indexBelow.x.toInt(), indexBelow.y.toInt()).tile == null &&
                (player.currentIndexX != indexBelow.x.toInt() || player.currentIndexY != indexBelow.y.toInt())

            ) {
                capacitor.nextIndex = indexBelow
                capacitor.nextPosition = Vector2(indexBelow.x * SPRITE_SIZE, indexBelow.y * SPRITE_SIZE)
                capacitor.isMoving = true
            }
        }
    }

    private fun updateMap() {
        if (!player.isMoving) {
            val layer: TiledMapTileLayer = map!!.layer("lev01") as TiledMapTileLayer
            for (i in 0 until layer.width) {
                for (j in 0 until layer.height) {
                    val cell = layer.getCell(i, j)
                    if (cell != null && cell.tile != null && (player.currentIndexX == i && player.currentIndexY == j)) {
                        if (cell.tile.id == MapObject.APPLE.tileId) {
                            keepMeAlive.applesEaten++
                            val sound = keepMeAlive.manager.get<Sound>("eatApple.wav")
                            soundId = sound.play(1f)
                        } else if (cell.tile.id == MapObject.ESC.tileId) {
                            val sound = keepMeAlive.manager.get<Sound>("saw.wav")
                            soundId = sound.play(1f)
                            if (keepMeAlive.currentLevel == 3) {
                                keepMeAlive.addScreen(GameOverScreen(keepMeAlive))
                                keepMeAlive.setScreen<GameOverScreen>()
                                keepMeAlive.removeScreen<GameScreen>()
                            } else {
                                keepMeAlive.addScreen(NextLevelScreen(keepMeAlive))
                                keepMeAlive.setScreen<NextLevelScreen>()
                                keepMeAlive.removeScreen<GameScreen>()
                            }
                            dispose()
                        } else {
                            val sound = keepMeAlive.manager.get<Sound>("eat.wav")
                            soundId = sound.play(1f)
                        }
                        cell.tile = null

                    }
                }
            }
        }
    }

    private fun updateCapacitors(delta: Float) {
        for (capacitor in capacitors) {
            capacitor.update(delta * 100)
        }
    }

    private fun updatePlayer(delta: Float) {
        if (delta == 0f) return

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.movePlayer(Direction.DOWN)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.movePlayer(Direction.UP)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.movePlayer(Direction.LEFT)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.movePlayer(Direction.RIGHT)
        }

        player.update(delta * 200)
    }
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

class Player {
    var map: TiledMap? = null
    var capacitors: MutableList<Capacitor>? = null
    var currentIndexX = 0
    var currentIndexY = 0
    var nextIndexX = 0
    var nextIndexY = 0

    var currentX = 0f
    var currentY = 0f
    var nextX = 0f
    var nextY = 0f
    var isMoving: Boolean = false

    fun update(delta: Float) {
        if (isMoving && (currentX != nextX || currentY != nextY)) {
            if (currentX != nextX) {
                if (currentX < nextX) {
                    if (currentX + delta >= nextX) {
                        currentX = nextX
                        currentIndexX = nextIndexX
                    } else {
                        currentX += delta
                    }
                } else if (currentX > nextX) {
                    if (currentX - delta <= nextX) {
                        currentX = nextX
                        currentIndexX = nextIndexX
                    } else {
                        currentX -= delta
                    }
                }
            }
            if (currentY != nextY) {
                if (currentY < nextY) {
                    if (currentY + delta >= nextY) {
                        currentY = nextY
                        currentIndexY = nextIndexY
                    } else {
                        currentY += delta
                    }
                } else if (currentY > nextY) {
                    if (currentY - delta <= nextY) {
                        currentY = nextY
                        currentIndexY = nextIndexY
                    } else {
                        currentY -= delta
                    }
                }
            }
        } else {
            isMoving = false
        }
    }

    fun movePlayer(direction: Direction) {
        if (isMoving) return

        val diff = when (direction) {
            Direction.UP -> Vector2(0f, 1f)
            Direction.DOWN -> Vector2(0f, -1f)
            Direction.LEFT -> Vector2(-1f, 0f)
            Direction.RIGHT -> Vector2(1f, 0f)
        }

        if (isWall(Vector2((currentIndexX + diff.x), (currentIndexY + diff.y)))) {
            return
        }
        if (isCapacitor(Vector2((currentIndexX + diff.x), (currentIndexY + diff.y)))) {
            return
        }
        nextY = currentY + (diff.y * SPRITE_SIZE)
        nextX = currentX + (diff.x * SPRITE_SIZE)
        nextIndexY = (currentIndexY + diff.y).toInt()
        nextIndexX = (currentIndexX + diff.x).toInt()
        isMoving = true
    }

    private fun isCapacitor(next: Vector2): Boolean {
        if (capacitors != null) {
            for (capacitor in capacitors!!) {
                if (capacitor.currentIndex.x == next.x && capacitor.currentIndex.y == next.y) {
                    return true
                }
                val dist = abs(capacitor.currentPosition.y - next.y * 32f)
                if (capacitor.isMoving && (dist <= 60f)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isWall(next: Vector2): Boolean {
        println(next)
        val layer: TiledMapTileLayer = map!!.layer("walls") as TiledMapTileLayer
        for (i in 0 until layer.width) {
            for (j in 0 until layer.height) {
                val cell = layer.getCell(i, j)
                if (cell != null && next.x.toInt() == i && next.y.toInt() == j) {
                    if (cell.tile.id == MapObject.WALL.tileId) {
                        return true
                    }
                }
            }
        }
        return false
    }
}