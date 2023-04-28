/*
 * Copyright (c) 2022 New Vector Ltd
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

package dev.getzen.element.features.start

import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.core.di.ActiveSessionHolder
import dev.getzen.element.core.di.NamedGlobalScope
import dev.getzen.element.core.extensions.startForegroundCompat
import dev.getzen.element.core.services.VectorAndroidService
import dev.getzen.element.features.notifications.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

/**
 * A simple foreground service that let the app (and the SDK) time to initialize.
 * Will self stop itself once the active session is set.
 */
@AndroidEntryPoint
class StartAppAndroidService : VectorAndroidService() {

    @NamedGlobalScope @Inject lateinit var globalScope: CoroutineScope
    @Inject lateinit var notificationUtils: NotificationUtils
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showStickyNotification()
        startPollingActiveSession()
        return START_STICKY
    }

    private fun startPollingActiveSession() {
        globalScope.launch {
            do {
                delay(1.seconds.inWholeMilliseconds)
            } while (activeSessionHolder.hasActiveSession().not())
            myStopSelf()
        }
    }

    private fun showStickyNotification() {
        val notificationId = Random.nextInt()
        val notification = notificationUtils.buildStartAppNotification()
        startForegroundCompat(notificationId, notification)
    }
}
