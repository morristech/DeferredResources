package com.backbase.android.res.deferred

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.Locale

internal val Context.primaryLocale: Locale
    get() = ConfigurationCompat.getLocales(resources.configuration)[0]
