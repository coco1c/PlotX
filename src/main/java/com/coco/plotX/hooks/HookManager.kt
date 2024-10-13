package com.coco.plotX.hooks

import com.coco.plotX.hooks.extensions.WorldGuardHook

class HookManager {
    var enabledHooks: MutableList<Extension> = ArrayList()

    fun hook() {
        enableWorldGuardHook()
    }
    private fun enableWorldGuardHook(){
        val worldGuard = WorldGuardHook()
        worldGuard.init()
        worldGuard.isEnabled?.let {
            enabledHooks.add(worldGuard)
        }
    }

    fun <T : Extension> getHookByClass(clazz: Class<T>): T? {
        return enabledHooks.find { clazz.isInstance(it) } as? T
    }

    override fun toString(): String {
        return "HookManager(enabledHooks=${enabledHooks.joinToString(", ")})"
    }
}