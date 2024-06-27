@file:OptIn(BetaInteropApi::class)

package fr.frankois944.kmpviewmodel

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import kotlin.reflect.KClass

public fun getOriginalKotlinClass(objCClass: ObjCClass): KClass<*>? = getOriginalKotlinClass(objCClass)
