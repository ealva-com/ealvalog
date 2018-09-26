/*
 * Copyright 2017 Eric A. Snell
 *
 * This file is part of eAlvaLog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ealva.ealvalog.coroutines

import com.ealva.ealvalog.MdcContext
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.CoroutineContext

typealias MdcKey = MdcCoroutineContext.Key

/**
 * Created by Eric A. Snell on 9/20/18.
 */
class MdcCoroutineContext(
  override val mdc: Map<String, String>?,
  override val ndc: List<String>?
) : AbstractCoroutineContextElement(MdcCoroutineContext), MdcContext {

  constructor(mdcContext: MdcContext) : this(mdcContext.mdc, mdcContext.ndc)

  /**
   * Key for [MdcContext] instance in the coroutine context.
   */
  companion object Key : CoroutineContext.Key<MdcCoroutineContext>

  override fun toString(): String = "MdcContext"
}
