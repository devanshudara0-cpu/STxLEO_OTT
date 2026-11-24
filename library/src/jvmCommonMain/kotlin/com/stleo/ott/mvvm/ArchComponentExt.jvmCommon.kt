package com.stleo.ott.mvvm

import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import kotlin.reflect.full.NoSuchPropertyException

actual fun <T> platformThrowAbleToResource(throwable: Throwable): Resource<T> {
    return when (throwable) {
        is NoSuchMethodException, is NoSuchFieldException, is NoSuchMethodError, is NoSuchFieldError, is NoSuchPropertyException -> {
            Resource.Failure(
                false,
                "Unable to load content from this server.\nPlease retry or select another server from the bottom right."
            )
        }
        is SocketTimeoutException, is InterruptedIOException -> {
            Resource.Failure(
                true,
                "Connection Timeout\nPlease try again later."
            )
        }
        is UnknownHostException -> {
            Resource.Failure(
                true,
                "Cannot connect to server, please check your network or try again later."
            )
        }
        is SSLHandshakeException -> {
            Resource.Failure(
                true,
                "Secure connection issue\nTry a VPN or DNS."
            )
        }
        else -> safeFail(throwable)
    }
}
