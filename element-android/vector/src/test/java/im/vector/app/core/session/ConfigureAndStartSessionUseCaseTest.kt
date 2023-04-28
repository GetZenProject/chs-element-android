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

package dev.getzen.element.core.session

import dev.getzen.element.core.extensions.startSyncing
import dev.getzen.element.core.session.clientinfo.UpdateMatrixClientInfoUseCase
import dev.getzen.element.features.session.coroutineScope
import dev.getzen.element.features.settings.devices.v2.notification.UpdateNotificationSettingsAccountDataUseCase
import dev.getzen.element.test.fakes.FakeContext
import dev.getzen.element.test.fakes.FakeNotificationsSettingUpdater
import dev.getzen.element.test.fakes.FakePushRulesUpdater
import dev.getzen.element.test.fakes.FakeSession
import dev.getzen.element.test.fakes.FakeVectorPreferences
import dev.getzen.element.test.fakes.FakeWebRtcCallManager
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ConfigureAndStartSessionUseCaseTest {

    private val fakeContext = FakeContext()
    private val fakeWebRtcCallManager = FakeWebRtcCallManager()
    private val fakeUpdateMatrixClientInfoUseCase = mockk<UpdateMatrixClientInfoUseCase>()
    private val fakeVectorPreferences = FakeVectorPreferences()
    private val fakeNotificationsSettingUpdater = FakeNotificationsSettingUpdater()
    private val fakePushRulesUpdater = FakePushRulesUpdater()
    private val fakeUpdateNotificationSettingsAccountDataUseCase = mockk<UpdateNotificationSettingsAccountDataUseCase>()

    private val configureAndStartSessionUseCase = ConfigureAndStartSessionUseCase(
            context = fakeContext.instance,
            webRtcCallManager = fakeWebRtcCallManager.instance,
            updateMatrixClientInfoUseCase = fakeUpdateMatrixClientInfoUseCase,
            vectorPreferences = fakeVectorPreferences.instance,
            notificationsSettingUpdater = fakeNotificationsSettingUpdater.instance,
            updateNotificationSettingsAccountDataUseCase = fakeUpdateNotificationSettingsAccountDataUseCase,
            pushRulesUpdater = fakePushRulesUpdater.instance,
    )

    @Before
    fun setup() {
        mockkStatic("dev.getzen.element.core.extensions.SessionKt")
        mockkStatic("dev.getzen.element.features.session.SessionCoroutineScopesKt")
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `given start sync needed and client info recording enabled when execute then it should be configured properly`() = runTest {
        // Given
        val aSession = givenASession()
        every { aSession.coroutineScope } returns this
        fakeWebRtcCallManager.givenCheckForProtocolsSupportIfNeededSucceeds()
        coJustRun { fakeUpdateMatrixClientInfoUseCase.execute(any()) }
        coJustRun { fakeUpdateNotificationSettingsAccountDataUseCase.execute(any()) }
        fakeVectorPreferences.givenIsClientInfoRecordingEnabled(isEnabled = true)
        fakeNotificationsSettingUpdater.givenOnSessionStarted(aSession)
        fakePushRulesUpdater.givenOnSessionStarted(aSession)

        // When
        configureAndStartSessionUseCase.execute(aSession, startSyncing = true)
        advanceUntilIdle()

        // Then
        verify { aSession.startSyncing(fakeContext.instance) }
        aSession.fakePushersService.verifyRefreshPushers()
        fakeWebRtcCallManager.verifyCheckForProtocolsSupportIfNeeded()
        coVerify {
            fakeUpdateMatrixClientInfoUseCase.execute(aSession)
            fakeUpdateNotificationSettingsAccountDataUseCase.execute(aSession)
        }
    }

    @Test
    fun `given start sync needed and client info recording disabled when execute then it should be configured properly`() = runTest {
        // Given
        val aSession = givenASession()
        every { aSession.coroutineScope } returns this
        fakeWebRtcCallManager.givenCheckForProtocolsSupportIfNeededSucceeds()
        coJustRun { fakeUpdateNotificationSettingsAccountDataUseCase.execute(any()) }
        fakeVectorPreferences.givenIsClientInfoRecordingEnabled(isEnabled = false)
        fakeNotificationsSettingUpdater.givenOnSessionStarted(aSession)
        fakePushRulesUpdater.givenOnSessionStarted(aSession)

        // When
        configureAndStartSessionUseCase.execute(aSession, startSyncing = true)
        advanceUntilIdle()

        // Then
        verify { aSession.startSyncing(fakeContext.instance) }
        aSession.fakePushersService.verifyRefreshPushers()
        fakeWebRtcCallManager.verifyCheckForProtocolsSupportIfNeeded()
        coVerify(inverse = true) {
            fakeUpdateMatrixClientInfoUseCase.execute(aSession)
        }
        coVerify {
            fakeUpdateNotificationSettingsAccountDataUseCase.execute(aSession)
        }
    }

    @Test
    fun `given a session and no start sync needed when execute then it should be configured properly`() = runTest {
        // Given
        val aSession = givenASession()
        every { aSession.coroutineScope } returns this
        fakeWebRtcCallManager.givenCheckForProtocolsSupportIfNeededSucceeds()
        coJustRun { fakeUpdateMatrixClientInfoUseCase.execute(any()) }
        coJustRun { fakeUpdateNotificationSettingsAccountDataUseCase.execute(any()) }
        fakeVectorPreferences.givenIsClientInfoRecordingEnabled(isEnabled = true)
        fakeNotificationsSettingUpdater.givenOnSessionStarted(aSession)
        fakePushRulesUpdater.givenOnSessionStarted(aSession)

        // When
        configureAndStartSessionUseCase.execute(aSession, startSyncing = false)
        advanceUntilIdle()

        // Then
        verify(inverse = true) { aSession.startSyncing(fakeContext.instance) }
        aSession.fakePushersService.verifyRefreshPushers()
        fakeWebRtcCallManager.verifyCheckForProtocolsSupportIfNeeded()
        coVerify {
            fakeUpdateMatrixClientInfoUseCase.execute(aSession)
            fakeUpdateNotificationSettingsAccountDataUseCase.execute(aSession)
        }
    }

    private fun givenASession(): FakeSession {
        val fakeSession = FakeSession()
        every { fakeSession.open() } just runs
        every { fakeSession.startSyncing(any()) } just runs
        fakeSession.fakePushersService.givenRefreshPushersSucceeds()
        return fakeSession
    }
}
