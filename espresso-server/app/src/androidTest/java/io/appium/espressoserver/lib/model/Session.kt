/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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

package io.appium.espressoserver.lib.model

import java.util.UUID

import io.appium.espressoserver.lib.handlers.exceptions.SessionNotCreatedException
import io.appium.espressoserver.lib.model.SessionParams.DesiredCapabilities

import io.appium.espressoserver.lib.helpers.AndroidLogger.logger

class Session private constructor(val id: String, val desiredCapabilities: DesiredCapabilities)// Instances of Session are private and only returned by 'createGlobalSession'
{
    companion object {
        // Only one session can run at a time so globally cache the current Session ID
        @Volatile
        var globalSession: Session? = null
            private set

        /**
         * Create a global session. Only one session can run per server instance so throw an exception
         * if one already is in progress
         *
         * @return Serializable Session object
         * @throws SessionNotCreatedException Thrown if a Session is already running
         */
        @Synchronized
        fun createGlobalSession(desiredCapabilities: DesiredCapabilities): Session? {
            if (globalSession != null) {
                logger.info(String.format("Got request for new session creation while the one " + "is still in progress. Overriding the old session having id %s",
                        globalSession!!.id))
            }
            val globalSessionId = UUID.randomUUID().toString()
            Session.globalSession = Session(globalSessionId, desiredCapabilities)
            return Session.globalSession
        }

        fun deleteGlobalSession() {
            Session.globalSession = null
        }
    }
}
