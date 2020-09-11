package dev.toastmc.client.module.render

import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.EntityUtils
import net.minecraft.entity.Entity
import java.util.concurrent.ConcurrentLinkedQueue

@ModuleManifest(
    label = "EntityCulling",
    description = "Culls rendering of entities you can't see.\nToo big to be part of NoRender.",
    category = Category.RENDER
)
class EntityCulling : Module() {
    companion object {
        val shouldNotCull = ConcurrentLinkedQueue<Entity>()
    }
    private val shouldNotCullTemp = ConcurrentLinkedQueue<Entity>()

    private val cullFinder = Thread() {
        while (true) {
            Thread.sleep(500)
            if (mc.world == null) continue
            val entityList = mc.world!!.entities
            if (entityList.count() == 0) continue
            for (i in 0 until entityList.count()) {
                val entity = mc.world!!.entities.elementAtOrNull(i)
                if (entity != null && !entity.removed && entity != mc.player!! && EntityUtils.isInView(entity.pos)) {
                    shouldNotCullTemp.add(entity)
                }
            }
            shouldNotCull.clear()
            shouldNotCull.addAll(shouldNotCullTemp)
            shouldNotCullTemp.clear()
        }
    }

    override fun onEnable() {
        super.onEnable()
        if (!cullFinder.isAlive) cullFinder.start()
    }

    override fun onDisable() {
        super.onDisable()
    }

}