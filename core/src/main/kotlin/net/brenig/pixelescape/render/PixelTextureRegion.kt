package net.brenig.pixelescape.render

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 *
 */
class PixelTextureRegion : TextureRegion {

    constructor() : super()
    constructor(texture: Texture?) : super(texture)
    constructor(texture: Texture?, width: Int, height: Int) : super(texture, width, height)
    constructor(texture: Texture?, x: Int, y: Int, width: Int, height: Int) : super(texture, x, y, width, height)
    constructor(texture: Texture?, u: Float, v: Float, u2: Float, v2: Float) : super(texture, u, v, u2, v2)
    constructor(region: TextureRegion?) : super(region)
    constructor(region: TextureRegion?, x: Int, y: Int, width: Int, height: Int) : super(region, x, y, width, height)

    private val srcX: Int
    private val srcY: Int

    init {
        srcX = (u * texture.width).toInt()
        srcY = (v * texture.height).toInt()
    }

    public override fun getTexture(): Texture {
        return super.getTexture()
    }

    fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int) {
        batch.draw(texture, x, y, 0F, 0F, width, height, 1F, 1F, 0F, this.srcX +  srcX, this.srcY + srcY, srcWidth, srcHeight, false, false)
    }

}

public fun TextureAtlas.createPixelRegion(s: String): PixelTextureRegion? {
    val region = this.findRegion(s)
    return region?.let { PixelTextureRegion(region) }
}

