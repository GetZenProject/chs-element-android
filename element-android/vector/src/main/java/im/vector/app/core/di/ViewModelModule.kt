/*
 * Copyright 2019 New Vector Ltd
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

package dev.getzen.element.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap
import dev.getzen.element.core.platform.ConfigurationViewModel
import dev.getzen.element.features.call.SharedKnownCallsViewModel
import dev.getzen.element.features.crypto.keysbackup.restore.KeysBackupRestoreFromKeyViewModel
import dev.getzen.element.features.crypto.keysbackup.restore.KeysBackupRestoreFromPassphraseViewModel
import dev.getzen.element.features.crypto.keysbackup.restore.KeysBackupRestoreSharedViewModel
import dev.getzen.element.features.crypto.keysbackup.setup.KeysBackupSetupSharedViewModel
import dev.getzen.element.features.discovery.DiscoverySharedViewModel
import dev.getzen.element.features.home.HomeSharedActionViewModel
import dev.getzen.element.features.home.room.detail.RoomDetailSharedActionViewModel
import dev.getzen.element.features.home.room.detail.timeline.action.MessageSharedActionViewModel
import dev.getzen.element.features.home.room.list.actions.RoomListQuickActionsSharedActionViewModel
import dev.getzen.element.features.home.room.list.actions.RoomListSharedActionViewModel
import dev.getzen.element.features.reactions.EmojiChooserViewModel
import dev.getzen.element.features.roomdirectory.RoomDirectorySharedActionViewModel
import dev.getzen.element.features.roomprofile.RoomProfileSharedActionViewModel
import dev.getzen.element.features.roomprofile.alias.detail.RoomAliasBottomSheetSharedActionViewModel
import dev.getzen.element.features.roomprofile.settings.historyvisibility.RoomHistoryVisibilitySharedActionViewModel
import dev.getzen.element.features.roomprofile.settings.joinrule.RoomJoinRuleSharedActionViewModel
import dev.getzen.element.features.spaces.SpacePreviewSharedActionViewModel
import dev.getzen.element.features.spaces.people.SpacePeopleSharedActionViewModel
import dev.getzen.element.features.userdirectory.UserListSharedActionViewModel

@InstallIn(ActivityComponent::class)
@Module
interface ViewModelModule {

    /**
     * ViewModels with @IntoMap will be injected by this factory.
     */
    @Binds
    fun bindViewModelFactory(factory: VectorViewModelFactory): ViewModelProvider.Factory

    /**
     *  Below are bindings for the androidx view models (which extend ViewModel). Will be converted to MvRx ViewModel in the future.
     */

    @Binds
    @IntoMap
    @ViewModelKey(EmojiChooserViewModel::class)
    fun bindEmojiChooserViewModel(viewModel: EmojiChooserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreFromKeyViewModel::class)
    fun bindKeysBackupRestoreFromKeyViewModel(viewModel: KeysBackupRestoreFromKeyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreSharedViewModel::class)
    fun bindKeysBackupRestoreSharedViewModel(viewModel: KeysBackupRestoreSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupRestoreFromPassphraseViewModel::class)
    fun bindKeysBackupRestoreFromPassphraseViewModel(viewModel: KeysBackupRestoreFromPassphraseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeysBackupSetupSharedViewModel::class)
    fun bindKeysBackupSetupSharedViewModel(viewModel: KeysBackupSetupSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfigurationViewModel::class)
    fun bindConfigurationViewModel(viewModel: ConfigurationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedKnownCallsViewModel::class)
    fun bindSharedActiveCallViewModel(viewModel: SharedKnownCallsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserListSharedActionViewModel::class)
    fun bindUserListSharedActionViewModel(viewModel: UserListSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeSharedActionViewModel::class)
    fun bindHomeSharedActionViewModel(viewModel: HomeSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageSharedActionViewModel::class)
    fun bindMessageSharedActionViewModel(viewModel: MessageSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomListQuickActionsSharedActionViewModel::class)
    fun bindRoomListQuickActionsSharedActionViewModel(viewModel: RoomListQuickActionsSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomAliasBottomSheetSharedActionViewModel::class)
    fun bindRoomAliasBottomSheetSharedActionViewModel(viewModel: RoomAliasBottomSheetSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomHistoryVisibilitySharedActionViewModel::class)
    fun bindRoomHistoryVisibilitySharedActionViewModel(viewModel: RoomHistoryVisibilitySharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomJoinRuleSharedActionViewModel::class)
    fun bindRoomJoinRuleSharedActionViewModel(viewModel: RoomJoinRuleSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomDirectorySharedActionViewModel::class)
    fun bindRoomDirectorySharedActionViewModel(viewModel: RoomDirectorySharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomDetailSharedActionViewModel::class)
    fun bindRoomDetailSharedActionViewModel(viewModel: RoomDetailSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomProfileSharedActionViewModel::class)
    fun bindRoomProfileSharedActionViewModel(viewModel: RoomProfileSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverySharedViewModel::class)
    fun bindDiscoverySharedViewModel(viewModel: DiscoverySharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SpacePreviewSharedActionViewModel::class)
    fun bindSpacePreviewSharedActionViewModel(viewModel: SpacePreviewSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SpacePeopleSharedActionViewModel::class)
    fun bindSpacePeopleSharedActionViewModel(viewModel: SpacePeopleSharedActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RoomListSharedActionViewModel::class)
    fun bindRoomListSharedActionViewModel(viewModel: RoomListSharedActionViewModel): ViewModel
}
