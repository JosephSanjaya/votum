package io.votum.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
