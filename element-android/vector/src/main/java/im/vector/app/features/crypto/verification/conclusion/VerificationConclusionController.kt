/*
 * Copyright 2020 New Vector Ltd
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

package dev.getzen.element.features.crypto.verification.conclusion

import com.airbnb.epoxy.EpoxyController
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.bottomSheetDividerItem
import dev.getzen.element.core.resources.ColorProvider
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.features.crypto.verification.epoxy.bottomSheetVerificationActionItem
import dev.getzen.element.features.crypto.verification.epoxy.bottomSheetVerificationBigImageItem
import dev.getzen.element.features.crypto.verification.epoxy.bottomSheetVerificationNoticeItem
import dev.getzen.element.features.html.EventHtmlRenderer
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import javax.inject.Inject

class VerificationConclusionController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val eventHtmlRenderer: EventHtmlRenderer
) : EpoxyController() {

    var listener: Listener? = null

    private var viewState: VerificationConclusionViewState? = null

    fun update(viewState: VerificationConclusionViewState) {
        this.viewState = viewState
        requestModelBuild()
    }

    override fun buildModels() {
        val state = viewState ?: return
        val host = this

        when (state.conclusionState) {
            ConclusionState.SUCCESS -> {
                bottomSheetVerificationNoticeItem {
                    id("notice")
                    notice(
                            host.stringProvider.getString(
                                    if (state.isSelfVerification) R.string.verification_conclusion_ok_self_notice
                                    else R.string.verification_conclusion_ok_notice
                            )
                                    .toEpoxyCharSequence()
                    )
                }

                bottomSheetVerificationBigImageItem {
                    id("image")
                    roomEncryptionTrustLevel(RoomEncryptionTrustLevel.Trusted)
                }

                bottomDone()
            }
            ConclusionState.WARNING -> {
                bottomSheetVerificationNoticeItem {
                    id("notice")
                    notice(host.stringProvider.getString(R.string.verification_conclusion_not_secure).toEpoxyCharSequence())
                }

                bottomSheetVerificationBigImageItem {
                    id("image")
                    roomEncryptionTrustLevel(RoomEncryptionTrustLevel.Warning)
                }

                bottomSheetVerificationNoticeItem {
                    id("warning_notice")
                    notice(host.eventHtmlRenderer.render(host.stringProvider.getString(R.string.verification_conclusion_compromised)).toEpoxyCharSequence())
                }

                bottomGotIt()
            }
            ConclusionState.INVALID_QR_CODE -> {
                bottomSheetVerificationNoticeItem {
                    id("invalid_qr")
                    notice(host.stringProvider.getString(R.string.verify_invalid_qr_notice).toEpoxyCharSequence())
                }

                bottomGotIt()
            }
            ConclusionState.CANCELLED -> {
                bottomSheetVerificationNoticeItem {
                    id("notice_cancelled")
                    notice(host.stringProvider.getString(R.string.verify_cancelled_notice).toEpoxyCharSequence())
                }

                bottomGotIt()
            }
        }
    }

    private fun bottomDone() {
        val host = this
        bottomSheetDividerItem {
            id("sep0")
        }

        bottomSheetVerificationActionItem {
            id("done")
            title(host.stringProvider.getString(R.string.done))
            titleColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            iconRes(R.drawable.ic_arrow_right)
            iconColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            listener { host.listener?.onButtonTapped(true) }
        }
    }

    private fun bottomGotIt() {
        val host = this
        bottomSheetDividerItem {
            id("sep0")
        }

        bottomSheetVerificationActionItem {
            id("got_it")
            title(host.stringProvider.getString(R.string.sas_got_it))
            titleColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            iconRes(R.drawable.ic_arrow_right)
            iconColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
            listener { host.listener?.onButtonTapped(false) }
        }
    }

    interface Listener {
        fun onButtonTapped(success: Boolean)
    }
}
