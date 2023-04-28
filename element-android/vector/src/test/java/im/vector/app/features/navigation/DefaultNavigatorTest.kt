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

package dev.getzen.element.features.navigation

import dev.getzen.element.test.fakes.FakeActiveSessionHolder
import dev.getzen.element.test.fakes.FakeAnalyticsTracker
import dev.getzen.element.test.fakes.FakeContext
import dev.getzen.element.test.fakes.FakeDebugNavigator
import dev.getzen.element.test.fakes.FakeSpaceStateHandler
import dev.getzen.element.test.fakes.FakeSupportedVerificationMethodsProvider
import dev.getzen.element.test.fakes.FakeVectorFeatures
import dev.getzen.element.test.fakes.FakeVectorPreferences
import dev.getzen.element.test.fakes.FakeWidgetArgsBuilder
import dev.getzen.element.test.fixtures.RoomSummaryFixture.aRoomSummary
import org.junit.Test

internal class DefaultNavigatorTest {

    private val sessionHolder = FakeActiveSessionHolder()
    private val vectorPreferences = FakeVectorPreferences()
    private val widgetArgsBuilder = FakeWidgetArgsBuilder()
    private val spaceStateHandler = FakeSpaceStateHandler()
    private val supportedVerificationMethodsProvider = FakeSupportedVerificationMethodsProvider()
    private val features = FakeVectorFeatures()
    private val analyticsTracker = FakeAnalyticsTracker()
    private val debugNavigator = FakeDebugNavigator()

    private val navigator = DefaultNavigator(
            sessionHolder.instance,
            vectorPreferences.instance,
            widgetArgsBuilder.instance,
            spaceStateHandler,
            supportedVerificationMethodsProvider.instance,
            features,
            analyticsTracker,
            debugNavigator,
    )

    /**
     * The below test is by no means all that we want to test in [DefaultNavigator].
     * Please add relevant tests as you make changes to or related to other functions in the class.
     */

    @Test
    fun `when switchToSpace, then current space set`() {
        val spaceId = "space-id"
        val spaceSummary = aRoomSummary(spaceId)
        sessionHolder.fakeSession.fakeRoomService.getRoomSummaryReturns(spaceSummary)

        navigator.switchToSpace(FakeContext().instance, spaceId, Navigator.PostSwitchSpaceAction.None)

        spaceStateHandler.verifySetCurrentSpace(spaceId)
    }
}
