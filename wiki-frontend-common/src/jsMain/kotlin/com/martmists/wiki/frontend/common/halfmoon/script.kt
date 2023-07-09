package com.martmists.wiki.frontend.halfmoon

external interface HalfmoonAPI {
    val darkModeOn: Boolean

    fun toggleDarkMode()
    fun getPreferredMode(): String
    fun toggleSidebar()
    fun toggleModal(modalId: String)
    fun makeId(length: Int): String
    fun toastAlert(alertId: String, timeShown: Int? = definedExternally)
    fun initStickyAlert(param: dynamic)
    fun onDOMContentLoaded()
}

external val halfmoon: HalfmoonAPI
