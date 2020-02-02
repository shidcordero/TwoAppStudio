package com.sample.testapplication.utils

object Constants {

    enum class ItemType(val id: Int) {
        URL(0);

        companion object {
            fun fromId(id: Int): ItemType? {
                for (at in values()) {
                    if (at.id == id) {
                        return at
                    }
                }
                return null
            }

            fun getId(name: String): Int {
                return valueOf(name).id
            }
        }
    }

    enum class DialogType {
        COMMON,
        SHOW_LOADING,
        DISMISS_LOADING,
        INTERNET
    }
}