package com.martmists.wiki.common.payload

import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(val jwtToken: String)
