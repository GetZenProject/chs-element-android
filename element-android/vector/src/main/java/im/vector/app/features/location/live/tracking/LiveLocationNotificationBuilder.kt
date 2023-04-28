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

package dev.getzen.element.features.location.live.tracking

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import dev.getzen.element.R
import dev.getzen.element.core.extensions.createIgnoredUri
import dev.getzen.element.core.platform.PendingIntentCompat
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.features.home.HomeActivity
import dev.getzen.element.features.home.room.detail.RoomDetailActivity
import dev.getzen.element.features.home.room.detail.arguments.TimelineArgs
import dev.getzen.element.features.location.live.map.LiveLocationMapViewActivity
import dev.getzen.element.features.location.live.map.LiveLocationMapViewArgs
import dev.getzen.element.features.notifications.NotificationActionIds
import dev.getzen.element.features.notifications.NotificationUtils
import dev.getzen.element.features.themes.ThemeUtils
import im.vector.lib.core.utils.timer.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveLocationNotificationBuilder @Inject constructor(
        private val context: Context,
        private val stringProvider: StringProvider,
        private val clock: Clock,
        private val actionIds: NotificationActionIds,
) {

    /**
     * Creates a notification that indicates the application is retrieving location even if it is in background or killed.
     * @param roomId the id of the room where a live location is shared
     */
    fun buildLiveLocationSharingNotification(roomId: String): Notification {
        return NotificationCompat.Builder(context, NotificationUtils.SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(stringProvider.getString(R.string.live_location_sharing_notification_title))
                .setContentText(stringProvider.getString(R.string.live_location_sharing_notification_description))
                .setSmallIcon(R.drawable.ic_attachment_live_location_white)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_LOCATION_SHARING)
                .setContentIntent(buildOpenLiveLocationMapIntent(roomId))
                .build()
    }

    private fun buildOpenLiveLocationMapIntent(roomId: String): PendingIntent? {
        val homeIntent = HomeActivity.newIntent(context, firstStartMainActivity = false)
        val roomIntent = RoomDetailActivity.newIntent(context, TimelineArgs(roomId = roomId, switchToParentSpace = true), firstStartMainActivity = false)
        val mapIntent = LiveLocationMapViewActivity.getIntent(
                context = context,
                liveLocationMapViewArgs = LiveLocationMapViewArgs(roomId = roomId),
                firstStartMainActivity = true
        )
        mapIntent.action = actionIds.tapToView
        // pending intent get reused by system, this will mess up the extra params, so put unique info to avoid that
        mapIntent.data = createIgnoredUri("openLiveLocationMap?$roomId")

        // Recreate the back stack
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(homeIntent)
                .addNextIntent(roomIntent)
                .addNextIntent(mapIntent)
                .getPendingIntent(
                        clock.epochMillis().toInt(),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
    }
}
