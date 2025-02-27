@file:Suppress("PackageDirectoryMismatch")

package org.koin.core.module.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

// https://github.com/InsertKoinIO/koin-annotations/issues/130#issuecomment-2189079092
// Koin KMP ViewModel is only available with Koin Compose Multiplatform
// We need to declared it in the commonMain of Koin Annotation won't work
public inline fun <reified T : ViewModel> Module.viewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
): KoinDefinition<T> = factory(qualifier, definition)
