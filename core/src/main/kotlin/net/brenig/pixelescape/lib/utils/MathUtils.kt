package net.brenig.pixelescape.lib.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

/**
 *
 */
object MathUtils {

    fun floorF(value: Float) = value.toInt().toFloat()

    fun getBoxInterSectWithAngle(xPos: Float, yPos: Float, xRadius: Float, yRadius: Float, angle: Float): Vector2 {
        // Move angle to range -Math.PI .. Math.PI
        val twoPI = (Math.PI * 2F).toFloat()
        var currentAngle = angle
        while (currentAngle < -Math.PI) {
            currentAngle += twoPI
        }

        while (currentAngle > Math.PI) {
            currentAngle -= twoPI
        }

        // find edge of view
        // Ref: http://stackoverflow.com/questions/4061576/finding-points-on-a-rectangle-at-a-given-angle
        val aa = xRadius * 2                                          // "a" in the diagram
        val bb = yRadius * 2                                         // "b"

        // Find our region (diagram)
        val rectAtan = MathUtils.atan2(bb, aa)
        val tanAngle = Math.tan(currentAngle.toDouble()).toFloat()

        val region: Int
        region = if ((currentAngle > -rectAtan)
                && (currentAngle <= rectAtan)) {
            1
        } else if ((currentAngle > rectAtan)
                && (currentAngle <= (Math.PI - rectAtan))) {
            2
        } else if ((currentAngle > (Math.PI - rectAtan))
                || (currentAngle <= -(Math.PI - rectAtan))) {
            3
        } else {
            4
        }

        val edgePoint = Vector2(xPos, yPos)

        var xFactor = 1F
        var yFactor = 1F

        when (region) {
            1, 2 -> yFactor = -1F
            3, 4 -> xFactor = -1F
        }

        if ((region == 1)
                || (region == 3)) {
            edgePoint.x += xFactor * (aa / 2F)                                     // "Z0"
            edgePoint.y += yFactor * (aa / 2F) * tanAngle
        } else                                                                        // region 2 or 4
        {
            edgePoint.x += xFactor * (bb / (2F * tanAngle))                        // "Z1"
            edgePoint.y += yFactor * (bb / 2F)
        }

        return edgePoint
    }

}