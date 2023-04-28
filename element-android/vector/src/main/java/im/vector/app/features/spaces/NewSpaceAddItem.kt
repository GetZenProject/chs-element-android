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

package dev.getzen.element.features.spaces

import android.content.res.ColorStateList
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import dev.getzen.element.R
import dev.getzen.element.core.epoxy.ClickListener
import dev.getzen.element.core.epoxy.VectorEpoxyHolder
import dev.getzen.element.core.epoxy.VectorEpoxyModel
import dev.getzen.element.core.epoxy.onClick
import dev.getzen.element.features.themes.ThemeUtils

@EpoxyModelClass
abstract class NewSpaceAddItem : VectorEpoxyModel<NewSpaceAddItem.Holder>(R.layout.item_new_space_add) {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var listener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.onClick(listener)

        holder.plus.imageTintList = ColorStateList.valueOf(ThemeUtils.getColor(holder.view.context, R.attr.vctr_content_primary))
    }

    class Holder : VectorEpoxyHolder() {
        val plus by bind<ImageView>(R.id.plus)
    }
}
