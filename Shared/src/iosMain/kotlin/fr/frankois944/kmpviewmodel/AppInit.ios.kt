@file:OptIn(BetaInteropApi::class)

package fr.frankois944.kmpviewmodel

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

public fun getOriginalKotlinClass(objCClass: ObjCClass): KClass<*>? = getOriginalKotlinClass(objCClass)
