package com.svbackend.natai

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}