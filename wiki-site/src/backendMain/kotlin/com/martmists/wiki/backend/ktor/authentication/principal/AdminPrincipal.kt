package com.martmists.wiki.backend.ktor.authentication.principal

import io.ktor.server.auth.*

class AdminPrincipal(val id: Int) : Principal
