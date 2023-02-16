package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.vulcan

import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.settings.GameSettings

class VulcanSpeeds : SpeedMode("Vulcan") {

    private val modeValue = ListValue("Vulcan-Mode", arrayOf("Hop", "Hop2", "YPort", "YPort2"), "Hop")


    // Variable
    private var portSwitcher = 0
    private var wasTimer = false
    private var jumpTicks = 0
    private var ticks = 0


    override fun onEnable() {

        when(modeValue.get()) {
            "Hop2" -> {
                sendLegacy()
            }
        }

        wasTimer = true
        mc.timer.timerSpeed = 1.0f
        portSwitcher = 0
    }

    override fun onDisable() {
        wasTimer = true
        mc.timer.timerSpeed = 1.0f
        portSwitcher = 0
    }

    override fun onUpdate() {

        when (modeValue.get()) {
            "Hop" -> {
                if (wasTimer) {
                    mc.timer.timerSpeed = 1.00f
                    wasTimer = false
                }
                if (Math.abs(mc.thePlayer.movementInput.moveStrafe) < 0.1f) {
                    mc.thePlayer.jumpMovementFactor = 0.026499f
                }else {
                    mc.thePlayer.jumpMovementFactor = 0.0244f
                }
                mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)

                if (MovementUtils.getSpeed() < 0.215f && !mc.thePlayer.onGround) {
                    MovementUtils.strafe(0.215f)
                }
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.gameSettings.keyBindJump.pressed = false
                    mc.thePlayer.jump()
                    if (!mc.thePlayer.isAirBorne) {
                        return //Prevent flag with Fly
                    }
                    mc.timer.timerSpeed = 1.25f
                    wasTimer = true
                    MovementUtils.strafe()
                    if(MovementUtils.getSpeed() < 0.5f) {
                        MovementUtils.strafe(0.4849f)
                    }
                }else if (!MovementUtils.isMoving()) {
                    mc.timer.timerSpeed = 1.00f
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }

            }
            "Hop2" -> {
                jumpTicks += 1

                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump()

                        jumpTicks = 0
                    } else {
                        if (jumpTicks > 3)
                            mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.08) * 0.98

                        MovementUtils.strafe(MovementUtils.getSpeed() * (1.01 - (Math.random() / 500)).toFloat() )
                    }
                } else {
                    mc.timer.timerSpeed = 1.00f
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
            }
            "YPort" -> {
                ticks++
                if (wasTimer) {
                    mc.timer.timerSpeed = 1.00f
                    wasTimer = false
                }
                mc.thePlayer.jumpMovementFactor = 0.0245f
                if (!mc.thePlayer.onGround && ticks > 3 && mc.thePlayer.motionY > 0) {
                    mc.thePlayer.motionY = -0.27
                }

                mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                if (MovementUtils.getSpeed() < 0.215f && !mc.thePlayer.onGround) {
                    MovementUtils.strafe(0.215f)
                }
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    ticks = 0
                    mc.gameSettings.keyBindJump.pressed = false
                    mc.thePlayer.jump()
                    if (!mc.thePlayer.isAirBorne) {
                        return //Prevent flag with Fly
                    }
                    mc.timer.timerSpeed = 1.2f
                    wasTimer = true
                    if(MovementUtils.getSpeed() < 0.48f) {
                        MovementUtils.strafe(0.48f)
                    }else{
                        MovementUtils.strafe((MovementUtils.getSpeed()*0.985).toFloat())
                    }
                }else if (!MovementUtils.isMoving()) {
                    mc.timer.timerSpeed = 1.00f
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }

            }
            "YPort2" -> {
                if (wasTimer) {
                    mc.timer.timerSpeed = 1.0f
                    wasTimer = false
                }
                if (portSwitcher > 1) {
                    mc.thePlayer.motionY = -0.2784
                    mc.timer.timerSpeed = 1.5f
                    wasTimer = true
                    if(portSwitcher > 1) {
                        portSwitcher = 0
                    }
                }
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe()
                    if(portSwitcher >= 1) {
                        mc.thePlayer.motionY = 0.2
                        mc.timer.timerSpeed = 1.5f
                    }
                    portSwitcher++
                }else if(MovementUtils.getSpeed() < 0.225){
                    MovementUtils.strafe(0.225f)
                }
            }
        }
    }
}

