// LiquidX Development by PrahXZ and Haflin with FDP Base modified. v2.0 R1
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class AAC5BHopSpeed : SpeedMode("AAC5BHop") {
    private var legitJump = false

    override fun onTick() {
        mc.timer.timerSpeed = 1f

        if (mc.thePlayer.isInWater) return

        if (MovementUtils.isMoving()) {
            when {
                mc.thePlayer.onGround -> {
                    if (legitJump) {
                        mc.thePlayer.jump()
                        legitJump = false
                        return
                    }
                    mc.thePlayer.motionY = 0.41
                    mc.thePlayer.onGround = false
                    MovementUtils.strafe(0.374f)
                }

                mc.thePlayer.motionY < 0.0 -> {
                    mc.thePlayer.speedInAir = 0.0201f
                    mc.timer.timerSpeed = 1.02f
                }

                else -> mc.timer.timerSpeed = 1.01f
            }
        } else {
            legitJump = true
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}