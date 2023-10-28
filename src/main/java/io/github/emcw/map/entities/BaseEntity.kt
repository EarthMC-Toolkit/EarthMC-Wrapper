package io.github.emcw.map.entities

import lombok.Getter

/**
 * A generic base class that all other entities inherit from. Holds a name and a reference to its parent.
 * @param <T> Determines the class type of the [.parent] reference.
</T> */
abstract class BaseEntity<T> {
    @JvmField @Getter var name: String? = null
    @JvmField @Getter var parent: T? = null

    protected fun setInfo(parent: T, name: String?) {
        this.parent = parent
        this.name = name
    }
}