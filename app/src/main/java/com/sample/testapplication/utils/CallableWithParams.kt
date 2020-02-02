package com.sample.testapplication.utils

import java.util.concurrent.Callable

abstract class CallableWithParams : Callable<Void?> {
    var params: Any? = null
}