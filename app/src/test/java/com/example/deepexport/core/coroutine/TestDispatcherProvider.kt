package com.example.deepexport.core.coroutine

import com.example.deepexport.core.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestDispatcherProvider(
    override val main: TestDispatcher,
    override val io: TestDispatcher,
    override val default: TestDispatcher
) : DispatcherProvider
