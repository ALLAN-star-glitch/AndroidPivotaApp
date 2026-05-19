// HttpClients.kt
package com.example.pivota.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthHttpClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UnauthHttpClient