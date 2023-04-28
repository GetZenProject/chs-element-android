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

package dev.getzen.element.features.voicebroadcast

import dev.getzen.element.features.voicebroadcast.listening.VoiceBroadcastPlayer
import dev.getzen.element.features.voicebroadcast.model.VoiceBroadcast
import dev.getzen.element.features.voicebroadcast.recording.usecase.PauseVoiceBroadcastUseCase
import dev.getzen.element.features.voicebroadcast.recording.usecase.ResumeVoiceBroadcastUseCase
import dev.getzen.element.features.voicebroadcast.recording.usecase.StartVoiceBroadcastUseCase
import dev.getzen.element.features.voicebroadcast.recording.usecase.StopVoiceBroadcastUseCase
import javax.inject.Inject

/**
 * Helper class to record voice broadcast.
 */
class VoiceBroadcastHelper @Inject constructor(
        private val startVoiceBroadcastUseCase: StartVoiceBroadcastUseCase,
        private val pauseVoiceBroadcastUseCase: PauseVoiceBroadcastUseCase,
        private val resumeVoiceBroadcastUseCase: ResumeVoiceBroadcastUseCase,
        private val stopVoiceBroadcastUseCase: StopVoiceBroadcastUseCase,
        private val voiceBroadcastPlayer: VoiceBroadcastPlayer,
) {
    suspend fun startVoiceBroadcast(roomId: String) = startVoiceBroadcastUseCase.execute(roomId)

    suspend fun pauseVoiceBroadcast(roomId: String) = pauseVoiceBroadcastUseCase.execute(roomId)

    suspend fun resumeVoiceBroadcast(roomId: String) = resumeVoiceBroadcastUseCase.execute(roomId)

    suspend fun stopVoiceBroadcast(roomId: String) = stopVoiceBroadcastUseCase.execute(roomId)

    fun playOrResumePlayback(voiceBroadcast: VoiceBroadcast) = voiceBroadcastPlayer.playOrResume(voiceBroadcast)

    fun pausePlayback() = voiceBroadcastPlayer.pause()

    fun stopPlayback() = voiceBroadcastPlayer.stop()

    fun seekTo(voiceBroadcast: VoiceBroadcast, positionMillis: Int, duration: Int) {
        voiceBroadcastPlayer.seekTo(voiceBroadcast, positionMillis, duration)
    }
}
