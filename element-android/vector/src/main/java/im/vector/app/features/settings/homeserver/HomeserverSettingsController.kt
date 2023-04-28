/*
 * Copyright (c) 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.getzen.element.features.settings.homeserver

import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.errorWithRetryItem
import dev.getzen.element.core.epoxy.loadingItem
import dev.getzen.element.core.error.ErrorFormatter
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.ui.list.genericWithValueItem
import dev.getzen.element.features.discovery.settingsCenteredImageItem
import dev.getzen.element.features.discovery.settingsInfoItem
import dev.getzen.element.features.discovery.settingsSectionTitleItem
import dev.getzen.element.features.settings.VectorPreferences
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.federation.FederationVersion
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.api.session.homeserver.RoomVersionStatus
import javax.inject.Inject

class HomeserverSettingsController @Inject constructor(
        private val stringProvider: StringProvider,
        private val errorFormatter: ErrorFormatter,
        private val vectorPreferences: VectorPreferences
) : TypedEpoxyController<HomeServerSettingsViewState>() {

    var callback: Callback? = null

    interface Callback {
        fun retry()
    }

    override fun buildModels(data: HomeServerSettingsViewState?) {
        data ?: return
        val host = this

        buildHeader(data)
        buildCapabilities(data)
        when (val federationVersion = data.federationVersion) {
            is Loading,
            is Uninitialized ->
                loadingItem {
                    id("loading")
                }
            is Fail ->
                errorWithRetryItem {
                    id("error")
                    text(host.errorFormatter.toHumanReadable(federationVersion.error))
                    listener { host.callback?.retry() }
                }
            is Success ->
                buildFederationVersion(federationVersion())
        }
    }

    private fun buildHeader(state: HomeServerSettingsViewState) {
        settingsCenteredImageItem {
            id("icon")
            drawableRes(R.drawable.ic_layers)
        }
        settingsSectionTitleItem {
            id("urlTitle")
            titleResId(R.string.hs_url)
        }
        settingsInfoItem {
            id("urlValue")
            helperText(state.homeserverUrl)
        }
        if (vectorPreferences.developerMode()) {
            settingsSectionTitleItem {
                id("urlApiTitle")
                titleResId(R.string.hs_client_url)
            }
            settingsInfoItem {
                id("urlApiValue")
                helperText(state.homeserverClientServerApiUrl)
            }
        }
    }

    private fun buildFederationVersion(federationVersion: FederationVersion) {
        settingsSectionTitleItem {
            id("nameTitle")
            titleResId(R.string.settings_server_name)
        }
        settingsInfoItem {
            id("nameValue")
            helperText(federationVersion.name)
        }
        settingsSectionTitleItem {
            id("versionTitle")
            titleResId(R.string.settings_server_version)
        }
        settingsInfoItem {
            id("versionValue")
            helperText(federationVersion.version)
        }
    }

    private fun buildCapabilities(data: HomeServerSettingsViewState) {
        val host = this
        settingsSectionTitleItem {
            id("uploadTitle")
            titleResId(R.string.settings_server_upload_size_title)
        }

        val limit = data.homeServerCapabilities.maxUploadFileSize

        settingsInfoItem {
            id("uploadValue")
            if (limit == HomeServerCapabilities.MAX_UPLOAD_FILE_SIZE_UNKNOWN) {
                helperTextResId(R.string.settings_server_upload_size_unknown)
            } else {
                helperText(host.stringProvider.getString(R.string.settings_server_upload_size_content, "${limit / 1048576L} MB"))
            }
        }

        if (vectorPreferences.developerMode()) {
            val roomCapabilities = data.homeServerCapabilities.roomVersions
            if (roomCapabilities != null) {
                settingsSectionTitleItem {
                    id("room_versions")
                    titleResId(R.string.settings_server_room_versions)
                }

                genericWithValueItem {
                    id("room_version_default")
                    title(host.stringProvider.getString(R.string.settings_server_default_room_version).toEpoxyCharSequence())
                    value(roomCapabilities.defaultRoomVersion)
                }

                roomCapabilities.supportedVersion.forEach {
                    genericWithValueItem {
                        id("room_version_${it.version}")
                        title(it.version.toEpoxyCharSequence())
                        value(
                                host.stringProvider.getString(
                                        when (it.status) {
                                            RoomVersionStatus.STABLE -> R.string.settings_server_room_version_stable
                                            RoomVersionStatus.UNSTABLE -> R.string.settings_server_room_version_unstable
                                        }
                                )
                        )
                    }
                }
            }
        }
    }
}
