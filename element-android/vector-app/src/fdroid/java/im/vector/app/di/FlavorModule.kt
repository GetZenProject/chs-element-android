/*
 * Copyright (c) 2021 New Vector Ltd
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

package dev.getzen.element.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.getzen.element.core.pushers.FcmHelper
import dev.getzen.element.core.resources.AppNameProvider
import dev.getzen.element.core.resources.DefaultAppNameProvider
import dev.getzen.element.core.resources.DefaultLocaleProvider
import dev.getzen.element.core.resources.LocaleProvider
import dev.getzen.element.core.services.GuardServiceStarter
import dev.getzen.element.fdroid.service.FDroidGuardServiceStarter
import dev.getzen.element.features.home.NightlyProxy
import dev.getzen.element.features.settings.VectorPreferences
import dev.getzen.element.features.settings.legals.FlavorLegals
import dev.getzen.element.push.fcm.FdroidFcmHelper

@InstallIn(SingletonComponent::class)
@Module
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(preferences: VectorPreferences, appContext: Context): GuardServiceStarter {
            return FDroidGuardServiceStarter(preferences, appContext)
        }

        @Provides
        fun provideNightlyProxy() = object : NightlyProxy {
            override fun canDisplayPopup() = false
            override fun isNightlyBuild() = false
            override fun updateApplication() = Unit
        }

        @Provides
        fun providesFlavorLegals() = object : FlavorLegals {
            override fun hasThirdPartyNotices() = false

            override fun navigateToThirdPartyNotices(context: Context) {
                // no op
            }
        }
    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: FdroidFcmHelper): FcmHelper

    @Binds
    abstract fun bindsLocaleProvider(localeProvider: DefaultLocaleProvider): LocaleProvider

    @Binds
    abstract fun bindsAppNameProvider(appNameProvider: DefaultAppNameProvider): AppNameProvider
}
