/*
 * Copyright 2019 New Vector Ltd
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

package dev.getzen.element.features.roomdirectory.roompreview

import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import dev.getzen.element.R
import dev.getzen.element.core.extensions.setTextOrHide
import dev.getzen.element.core.platform.ButtonStateView
import dev.getzen.element.core.platform.VectorBaseFragment
import dev.getzen.element.core.utils.styleMatchingText
import dev.getzen.element.core.utils.tappableMatchingText
import dev.getzen.element.databinding.FragmentRoomPreviewNoPreviewBinding
import dev.getzen.element.features.analytics.plan.MobileScreen
import dev.getzen.element.features.analytics.plan.ViewRoom
import dev.getzen.element.features.home.AvatarRenderer
import dev.getzen.element.features.navigation.Navigator
import dev.getzen.element.features.roomdirectory.JoinState
import dev.getzen.element.features.settings.VectorSettingsActivity
import dev.getzen.element.features.themes.ThemeUtils
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.util.MatrixItem
import javax.inject.Inject

/**
 * Note: this Fragment is also used for world readable room for the moment.
 */
@AndroidEntryPoint
class RoomPreviewNoPreviewFragment :
        VectorBaseFragment<FragmentRoomPreviewNoPreviewBinding>() {

    @Inject lateinit var avatarRenderer: AvatarRenderer

    private val roomPreviewViewModel: RoomPreviewViewModel by fragmentViewModel()
    private val roomPreviewData: RoomPreviewData by args()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRoomPreviewNoPreviewBinding {
        return FragmentRoomPreviewNoPreviewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(views.roomPreviewNoPreviewToolbar)
                .allowBack()

        views.roomPreviewNoPreviewJoin.commonClicked = { roomPreviewViewModel.handle(RoomPreviewAction.Join) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsScreenName = MobileScreen.ScreenName.RoomPreview
    }

    override fun invalidate() = withState(roomPreviewViewModel) { state ->

        views.roomPreviewNoPreviewJoin.render(
                when (state.roomJoinState) {
                    JoinState.NOT_JOINED -> ButtonStateView.State.Button
                    JoinState.JOINING -> ButtonStateView.State.Loading
                    JoinState.JOINED -> ButtonStateView.State.Loaded
                    JoinState.JOINING_ERROR -> ButtonStateView.State.Error
                }
        )

        if (state.lastError == null) {
            views.roomPreviewNoPreviewError.isVisible = false
        } else {
            views.roomPreviewNoPreviewError.isVisible = true
            views.roomPreviewNoPreviewError.text = errorFormatter.toHumanReadable(state.lastError)
        }

        if (state.roomJoinState == JoinState.JOINED) {
            // Quit this screen
            requireActivity().finish()
            // Open room
            if (state.roomType == RoomType.SPACE) {
                navigator.switchToSpace(requireActivity(), state.roomId, Navigator.PostSwitchSpaceAction.None)
            } else {
                navigator.openRoom(
                        context = requireActivity(),
                        roomId = state.roomId,
                        eventId = roomPreviewData.eventId,
                        buildTask = roomPreviewData.buildTask,
                        trigger = ViewRoom.Trigger.MobileRoomPreview
                )
            }
        }

        val bestName = state.roomName ?: state.roomAlias ?: state.roomId
        when (state.peekingState) {
            is Loading -> {
                views.roomPreviewPeekingProgress.isVisible = true
                views.roomPreviewNoPreviewJoin.isVisible = false
            }
            is Success -> {
                views.roomPreviewPeekingProgress.isVisible = false
                when (state.peekingState.invoke()) {
                    PeekingState.FOUND -> {
                        // show join buttons
                        views.roomPreviewNoPreviewJoin.isVisible = true
                        renderState(bestName, state.matrixItem(), state.roomTopic)
                        if (state.fromEmailInvite != null && !state.isEmailBoundToAccount) {
                            views.roomPreviewNoPreviewLabel.text =
                                    span {
                                        span {
                                            textColor = ThemeUtils.getColor(requireContext(), R.attr.vctr_content_primary)
                                            text = if (state.roomType == RoomType.SPACE) {
                                                getString(R.string.this_invite_to_this_space_was_sent, state.fromEmailInvite.email)
                                            } else {
                                                getString(R.string.this_invite_to_this_room_was_sent, state.fromEmailInvite.email)
                                            }
                                                    .toSpannable()
                                                    .styleMatchingText(state.fromEmailInvite.email, Typeface.BOLD)
                                        }
                                        +"\n"
                                        span {
                                            text = getString(
                                                    R.string.link_this_email_with_your_account,
                                                    getString(R.string.link_this_email_settings_link)
                                            )
                                                    .toSpannable()
                                                    .tappableMatchingText(getString(R.string.link_this_email_settings_link), object : ClickableSpan() {
                                                        override fun onClick(widget: View) {
                                                            navigator.openSettings(
                                                                    requireContext(),
                                                                    VectorSettingsActivity.EXTRA_DIRECT_ACCESS_DISCOVERY_SETTINGS
                                                            )
                                                        }
                                                    })
                                        }
                                    }
                            views.roomPreviewNoPreviewLabel.movementMethod = LinkMovementMethod.getInstance()
                            views.roomPreviewNoPreviewJoin.commonClicked = {
                                roomPreviewViewModel.handle(RoomPreviewAction.JoinThirdParty)
                            }
                        }
                    }
                    PeekingState.NO_ACCESS -> {
                        views.roomPreviewNoPreviewJoin.isVisible = true
                        views.roomPreviewNoPreviewLabel.isVisible = true
                        views.roomPreviewNoPreviewLabel.setText(R.string.room_preview_no_preview_join)
                        renderState(bestName, state.matrixItem().takeIf { state.roomAlias != null }, state.roomTopic)
                    }
                    else -> {
                        views.roomPreviewNoPreviewJoin.isVisible = false
                        views.roomPreviewNoPreviewLabel.isVisible = true
                        views.roomPreviewNoPreviewLabel.setText(R.string.room_preview_not_found)
                        renderState(bestName, null, state.roomTopic)
                    }
                }
            }
            else -> {
                // Render with initial state, no peeking
                views.roomPreviewPeekingProgress.isVisible = false
                views.roomPreviewNoPreviewJoin.isVisible = true
                renderState(bestName, state.matrixItem(), state.roomTopic)
                views.roomPreviewNoPreviewLabel.isVisible = false
            }
        }
    }

    private fun renderState(roomName: String, matrixItem: MatrixItem?, topic: String?) {
        // Toolbar
        if (matrixItem != null) {
            views.roomPreviewNoPreviewToolbarAvatar.isVisible = true
            views.roomPreviewNoPreviewAvatar.isVisible = true
            avatarRenderer.render(matrixItem, views.roomPreviewNoPreviewToolbarAvatar)
            avatarRenderer.render(matrixItem, views.roomPreviewNoPreviewAvatar)
        } else {
            views.roomPreviewNoPreviewToolbarAvatar.isVisible = false
            views.roomPreviewNoPreviewAvatar.isVisible = false
        }
        views.roomPreviewNoPreviewToolbarTitle.text = roomName

        // Screen
        views.roomPreviewNoPreviewName.text = roomName
        views.roomPreviewNoPreviewTopic.setTextOrHide(topic)
    }
}
