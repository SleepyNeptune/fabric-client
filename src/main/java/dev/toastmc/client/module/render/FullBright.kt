package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import java.util.*

@ModuleManifest(
        label = "Fullbright",
        description = "Brightness",
        category = Category.RENDER
)
class FullBright : Module() {

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        increaseGamma()
    })

    override fun onDisable() {
        mc.options.gamma = previousGamma
        increasedGamma = false
        if (mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            if (Objects.requireNonNull(mc.player!!.getStatusEffect(StatusEffects.NIGHT_VISION))!!.amplifier == 42069) mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        previousGamma = mc.options.gamma
        mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, 42069, 42069))
        EVENT_BUS.subscribe(onTickEvent)
    }

    private fun increaseGamma() {
        mc.options.gamma = 1000.0
        increasedGamma = true
    }

    companion object {
        private var increasedGamma = false
        private var previousGamma = 0.0
    }
}