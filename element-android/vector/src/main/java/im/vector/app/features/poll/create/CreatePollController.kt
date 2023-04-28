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

package dev.getzen.element.features.poll.create

import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.airbnb.epoxy.EpoxyController
import dev.getzen.element.R
import dev.getzen.element.core.resources.ColorProvider
import dev.getzen.element.core.resources.StringProvider
import dev.getzen.element.core.ui.list.ItemStyle
import dev.getzen.element.core.ui.list.genericButtonItem
import dev.getzen.element.core.ui.list.genericItem
import dev.getzen.element.features.form.formEditTextItem
import dev.getzen.element.features.form.formEditTextWithDeleteItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import org.matrix.android.sdk.api.session.room.model.message.PollType
import javax.inject.Inject

class CreatePollController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider
) : EpoxyController() {

    private var state: CreatePollViewState? = null
    var callback: Callback? = null

    fun setData(state: CreatePollViewState) {
        this.state = state
        requestModelBuild()
    }

    override fun buildModels() {
        val currentState = state ?: return
        val host = this

        genericItem {
            id("poll_type_title")
            style(ItemStyle.BIG_TEXT)
            title(host.stringProvider.getString(R.string.poll_type_title).toEpoxyCharSequence())
        }

        pollTypeSelectionItem {
            id("poll_type_selection")
            pollType(currentState.pollType)
            pollTypeChangedListener { _, id ->
                host.callback?.onPollTypeChanged(
                        if (id == R.id.openPollTypeRadioButton) {
                            PollType.DISCLOSED_UNSTABLE
                        } else {
                            PollType.UNDISCLOSED_UNSTABLE
                        }
                )
            }
        }

        genericItem {
            id("question_title")
            style(ItemStyle.BIG_TEXT)
            title(host.stringProvider.getString(R.string.create_poll_question_title).toEpoxyCharSequence())
        }

        val questionImeAction = if (currentState.options.isEmpty()) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT

        formEditTextItem {
            id("question")
            value(currentState.question)
            hint(host.stringProvider.getString(R.string.create_poll_question_hint))
            singleLine(true)
            imeOptions(questionImeAction)
            maxLength(340)
            onTextChange {
                host.callback?.onQuestionChanged(it)
            }
        }

        genericItem {
            id("options_title")
            style(ItemStyle.BIG_TEXT)
            title(host.stringProvider.getString(R.string.create_poll_options_title).toEpoxyCharSequence())
        }

        currentState.options.forEachIndexed { index, option ->
            val imeOptions = if (index == currentState.options.size - 1) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
            formEditTextWithDeleteItem {
                id("option_$index")
                value(option)
                hint(host.stringProvider.getString(R.string.create_poll_options_hint, (index + 1)))
                singleLine(true)
                imeOptions(imeOptions)
                maxLength(340)
                onTextChange {
                    host.callback?.onOptionChanged(index, it)
                }
                onDeleteClicked {
                    host.callback?.onDeleteOption(index)
                }
            }
        }

        if (currentState.canAddMoreOptions) {
            genericButtonItem {
                id("add_option")
                text(host.stringProvider.getString(R.string.create_poll_add_option))
                textColor(host.colorProvider.getColor(R.color.palette_element_green))
                gravity(Gravity.START)
                bold(true)
                highlight(false)
                buttonClickAction {
                    host.callback?.onAddOption()
                }
            }
        }
    }

    interface Callback {
        fun onQuestionChanged(question: String)
        fun onOptionChanged(index: Int, option: String)
        fun onDeleteOption(index: Int)
        fun onAddOption()
        fun onPollTypeChanged(type: PollType)
    }
}
