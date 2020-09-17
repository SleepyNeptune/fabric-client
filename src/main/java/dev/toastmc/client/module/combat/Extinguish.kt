package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.ItemUtil
import dev.toastmc.client.util.WorldUtil.placeBlock
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RaycastContext
import net.minecraft.world.dimension.DimensionType

@ModuleManifest(
        label = "Extinguish",
        category = Category.COMBAT
)
class Extinguish : Module() {
    @Setting(name = "Rotate") var rotate = false
    @Setting(name = "Macro") var macro = false

    var watered = false
    var pos: BlockPos? = null

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player!!.isOnFire && mc.world!!.dimension != DimensionType.THE_NETHER_ID) {
            if (!watered) {
                pos = mc.world!!.raycast(RaycastContext(mc.player!!.pos, mc.player!!.pos.add(0.0, -1.0, 0.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player!!))?.blockPos
                if (pos != null) {
                    ItemUtil.equip(ItemUtil.getItemSlotInHotbar(Items.WATER_BUCKET))
                    placeBlock(pos!!, Hand.MAIN_HAND, rotate)
                    watered = true
                }
            } else {
                placeBlock(pos!!, Hand.MAIN_HAND, rotate)
                pos = null
                watered = false
            }
        }

        if (pos == null && macro) disable()
    })
}