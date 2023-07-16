package com.martmists.wiki.common.payload

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(val username: String, val password: String)
