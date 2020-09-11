package dev.toastmc.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Saddleable
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.atan
import kotlin.math.sqrt


object EntityUtils {
    private val mc = MinecraftClient.getInstance()

    fun notSelf(e: Entity): Boolean {
        return e !== mc.player && e !== mc.cameraEntity
    }

    fun isAnimal(e: Entity?): Boolean {
        return e is AnimalEntity ||
                e is AmbientEntity ||
                e is WaterCreatureEntity ||
                e is GolemEntity && !e.handSwinging ||
                e is VillagerEntity
    }

    fun isLiving(e: Entity?): Boolean {
        return e is LivingEntity
    }

    fun isHostile(e: Entity?): Boolean {
        return e is HostileEntity && e !is PiglinEntity && e !is EndermanEntity ||
                e is PiglinEntity && e.isAngryAt(mc.player) ||
                e is WolfEntity && e.isAttacking && e.ownerUuid !== mc.player!!.uuid ||
                e is EndermanEntity && e.isAngry ||
                e is GolemEntity && e.handSwinging
    }

    fun isNeutral(e: Entity?): Boolean {
        return e is PiglinEntity && !e.isAngryAt(mc.player) ||
                e is WolfEntity && (!e.isAttacking || e.ownerUuid === mc.player!!.uuid) ||
                e is EndermanEntity && !e.isAngry ||
                e is GolemEntity && !e.handSwinging
    }
    fun isVehicle(e: Entity?): Boolean {
        return e is BoatEntity ||
                e is MinecartEntity ||
                (e is Saddleable && e.isSaddled)
    }

    fun isInView(point: Vec3d): Boolean {
        if (mc.player == null) return false;
        val pos = mc.gameRenderer.camera.pos
        val yToPoint = atan(sqrt((point.x - pos.x) * (point.x - pos.x) + (point.z - pos.z) * (point.z - pos.z)) / pos.y - point.y) * (180.0 / Math.PI)
        val yToLowSide = 180.0 + (mc.gameRenderer.camera!!.pitch - 320.0)
        if (yToPoint !in yToLowSide..(yToLowSide + mc.options.fov + 90.0)) return false

        val horizontalFov = mc.options.fov / mc.window.height * mc.window.width
        val xDistance = point.x - pos.x
        val zDistance = point.z - pos.z
        val zToPoint = atan(xDistance / zDistance) * (180.0 / Math.PI)
        // camera yaw confuses me
        val actualYaw = mc.player!!.yaw + if (mc.player!!.yaw < -180.0) 360.0 else 0.0
        val zToZSide = actualYaw - (horizontalFov / 2)
        val zToXSide = actualYaw + (horizontalFov / 2)
        return zToPoint in zToZSide..zToXSide
    }

    fun isInView(x: Double, y: Double, z: Double): Boolean {
        return isInView(Vec3d(x, y, z))
    }

    fun isInView(box: Box): Boolean {
        val centerX = (box.maxX + box.minX) / 2.0
        val centerZ = (box.maxZ + box.minZ) / 2.0
        return  isInView(box.maxX,  box.maxY,                                       box.maxZ) ||
                isInView(box.maxX,  box.maxY,                                       box.minZ) ||
                isInView(box.maxX,  box.minY,                                       box.maxZ) ||
                isInView(box.maxX,  box.minY,                                       box.minZ) ||
                isInView(box.minX,  box.maxY,                                       box.maxZ) ||
                isInView(box.minX,  box.maxY,                                       box.minZ) ||
                isInView(box.minX,  box.minY,                                       box.maxZ) ||
                isInView(box.minX,  box.minY,                                       box.minZ) ||
                isInView(centerX,   box.maxY,                                       centerZ ) ||
                isInView(centerX,   box.maxY - (box.maxY - box.minY) / 4.0,         centerZ ) ||
                isInView(centerX,   box.maxY - (box.maxY - box.minY) * 3.0 / 4.0,   centerZ ) ||
                isInView(centerX,  box.minY,                                        centerZ )
    }
}