package io.github.emcw

import lombok.Getter

/**
 * The main entrypoint of this library. Allows initializing maps independently.
 *
 * Holds an instance of [Aurora] and [Nova] maps as well as itself.
 */
class EMCWrapper {
    @Getter
    lateinit var Aurora: EMCMap

    @Getter
    lateinit var Nova: EMCMap

    /**
     * Returns a new wrapper instance. Both maps are initialized by default.
     */
    constructor() {
        initMaps(true, true)
    }

    constructor(aurora: EMCMap?, nova: EMCMap?) {
        initMaps(aurora, nova)
    }

    /**
     * Returns a new [EMCWrapper] instance.
     * Maps may be initialized independently by passing their respective boolean values.
     * @param aurora Enable initialization of the [.Aurora] map.
     * @param nova Enable initialization of the [.Nova] map.
     */
    constructor(aurora: Boolean, nova: Boolean) {
        initMaps(aurora, nova)
    }

    private fun initMaps(aurora: Boolean, nova: Boolean) {
        if (aurora) Aurora = EMCMap("aurora")
        if (nova) Nova = EMCMap("nova")
        instance = this
    }

    private fun initMaps(aurora: EMCMap?, nova: EMCMap?) {
        if (aurora != null) Aurora = aurora
        if (nova != null) Nova = nova
        instance = this
    }

    companion object {
        lateinit var instance: EMCWrapper

        @JvmStatic
        fun instance(): EMCWrapper {
            return instance
        }
    }
}