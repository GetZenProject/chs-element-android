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

package dev.getzen.element.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.getzen.element.BuildConfig
import dev.getzen.element.config.Analytics
import dev.getzen.element.config.Config
import dev.getzen.element.config.KeySharingStrategy
import dev.getzen.element.features.analytics.AnalyticsConfig
import dev.getzen.element.features.call.webrtc.VoipConfig
import dev.getzen.element.features.crypto.keysrequest.OutboundSessionKeySharingStrategy
import dev.getzen.element.features.home.room.detail.composer.voice.VoiceMessageConfig
import dev.getzen.element.features.location.LocationSharingConfig
import dev.getzen.element.features.raw.wellknown.CryptoConfig

@InstallIn(SingletonComponent::class)
@Module
object ConfigurationModule {

    @Provides
    fun providesAnalyticsConfig(): AnalyticsConfig {
        val config: Analytics = when (BuildConfig.BUILD_TYPE) {
            "debug" -> Config.DEBUG_ANALYTICS_CONFIG
            "nightly" -> Config.NIGHTLY_ANALYTICS_CONFIG
            "release" -> Config.RELEASE_ANALYTICS_CONFIG
            else -> throw IllegalStateException("Unhandled build type: ${BuildConfig.BUILD_TYPE}")
        }
        return when (config) {
            Analytics.Disabled -> AnalyticsConfig(isEnabled = false, "", "", "", "", "")
            is Analytics.Enabled -> AnalyticsConfig(
                    isEnabled = true,
                    postHogHost = config.postHogHost,
                    postHogApiKey = config.postHogApiKey,
                    policyLink = config.policyLink,
                    sentryDSN = config.sentryDSN,
                    sentryEnvironment = config.sentryEnvironment
            )
        }
    }

    @Provides
    fun providesVoiceMessageConfig() = VoiceMessageConfig(
            lengthLimitMs = Config.VOICE_MESSAGE_LIMIT_MS
    )

    @Provides
    fun providesCryptoConfig() = CryptoConfig(
            fallbackKeySharingStrategy = when (Config.KEY_SHARING_STRATEGY) {
                KeySharingStrategy.WhenSendingEvent -> OutboundSessionKeySharingStrategy.WhenSendingEvent
                KeySharingStrategy.WhenEnteringRoom -> OutboundSessionKeySharingStrategy.WhenSendingEvent
                KeySharingStrategy.WhenTyping -> OutboundSessionKeySharingStrategy.WhenSendingEvent
            }
    )

    @Provides
    fun providesLocationSharingConfig() = LocationSharingConfig(
            mapTilerKey = Config.LOCATION_MAP_TILER_KEY,
    )

    @Provides
    fun providesVoipConfig() = VoipConfig(
            handleCallAssertedIdentityEvents = Config.HANDLE_CALL_ASSERTED_IDENTITY_EVENTS
    )
}
