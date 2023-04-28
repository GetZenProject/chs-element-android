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

package dev.getzen.element.features.onboarding.ftueauth

import dev.getzen.element.test.fixtures.aDummyStage
import dev.getzen.element.test.fixtures.aMsisdnStage
import dev.getzen.element.test.fixtures.aRecaptchaStage
import dev.getzen.element.test.fixtures.aTermsStage
import dev.getzen.element.test.fixtures.anEmailStage
import dev.getzen.element.test.fixtures.anOtherStage
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class MatrixOrgRegistrationStagesComparatorTest {

    @Test
    fun `when ordering stages, then prioritizes email`() {
        val input = listOf(
                aDummyStage(),
                anOtherStage(),
                aMsisdnStage(),
                anEmailStage(),
                aRecaptchaStage(),
                aTermsStage()
        )

        val result = input.sortedWith(MatrixOrgRegistrationStagesComparator())

        result shouldBeEqualTo listOf(
                anEmailStage(),
                aMsisdnStage(),
                aTermsStage(),
                aRecaptchaStage(),
                anOtherStage(),
                aDummyStage()
        )
    }
}
