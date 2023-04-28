/*
 * Copyright 2018 New Vector Ltd
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
package dev.getzen.element.push.fcm

import androidx.fragment.app.Fragment
import dev.getzen.element.core.pushers.UnifiedPushHelper
import dev.getzen.element.fdroid.features.settings.troubleshoot.TestAutoStartBoot
import dev.getzen.element.fdroid.features.settings.troubleshoot.TestBackgroundRestrictions
import dev.getzen.element.fdroid.features.settings.troubleshoot.TestBatteryOptimization
import dev.getzen.element.features.VectorFeatures
import dev.getzen.element.features.push.NotificationTroubleshootTestManagerFactory
import dev.getzen.element.features.settings.troubleshoot.NotificationTroubleshootTestManager
import dev.getzen.element.features.settings.troubleshoot.TestAccountSettings
import dev.getzen.element.features.settings.troubleshoot.TestAvailableUnifiedPushDistributors
import dev.getzen.element.features.settings.troubleshoot.TestCurrentUnifiedPushDistributor
import dev.getzen.element.features.settings.troubleshoot.TestDeviceSettings
import dev.getzen.element.features.settings.troubleshoot.TestEndpointAsTokenRegistration
import dev.getzen.element.features.settings.troubleshoot.TestNotification
import dev.getzen.element.features.settings.troubleshoot.TestPushFromPushGateway
import dev.getzen.element.features.settings.troubleshoot.TestPushRulesSettings
import dev.getzen.element.features.settings.troubleshoot.TestSystemSettings
import dev.getzen.element.features.settings.troubleshoot.TestUnifiedPushEndpoint
import dev.getzen.element.features.settings.troubleshoot.TestUnifiedPushGateway
import javax.inject.Inject

class FdroidNotificationTroubleshootTestManagerFactory @Inject constructor(
        private val unifiedPushHelper: UnifiedPushHelper,
        private val testSystemSettings: TestSystemSettings,
        private val testAccountSettings: TestAccountSettings,
        private val testDeviceSettings: TestDeviceSettings,
        private val testPushRulesSettings: TestPushRulesSettings,
        private val testCurrentUnifiedPushDistributor: TestCurrentUnifiedPushDistributor,
        private val testUnifiedPushGateway: TestUnifiedPushGateway,
        private val testUnifiedPushEndpoint: TestUnifiedPushEndpoint,
        private val testAvailableUnifiedPushDistributors: TestAvailableUnifiedPushDistributors,
        private val testEndpointAsTokenRegistration: TestEndpointAsTokenRegistration,
        private val testPushFromPushGateway: TestPushFromPushGateway,
        private val testAutoStartBoot: TestAutoStartBoot,
        private val testBackgroundRestrictions: TestBackgroundRestrictions,
        private val testBatteryOptimization: TestBatteryOptimization,
        private val testNotification: TestNotification,
        private val vectorFeatures: VectorFeatures,
) : NotificationTroubleshootTestManagerFactory {

    override fun create(fragment: Fragment): NotificationTroubleshootTestManager {
        val mgr = NotificationTroubleshootTestManager(fragment)
        mgr.addTest(testSystemSettings)
        mgr.addTest(testAccountSettings)
        mgr.addTest(testDeviceSettings)
        mgr.addTest(testPushRulesSettings)
        if (vectorFeatures.allowExternalUnifiedPushDistributors()) {
            mgr.addTest(testAvailableUnifiedPushDistributors)
            mgr.addTest(testCurrentUnifiedPushDistributor)
        }
        if (unifiedPushHelper.isBackgroundSync()) {
            mgr.addTest(testAutoStartBoot)
            mgr.addTest(testBackgroundRestrictions)
            mgr.addTest(testBatteryOptimization)
        } else {
            mgr.addTest(testUnifiedPushGateway)
            mgr.addTest(testUnifiedPushEndpoint)
            mgr.addTest(testEndpointAsTokenRegistration)
            mgr.addTest(testPushFromPushGateway)
        }
        mgr.addTest(testNotification)
        return mgr
    }
}
