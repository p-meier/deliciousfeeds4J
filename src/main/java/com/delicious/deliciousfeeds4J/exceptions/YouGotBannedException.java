/*
 * Copyright (c) 2013 by Patrick Meier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.delicious.deliciousfeeds4J.exceptions;

/**
 * An exception that gets thrown when the response code was 503 - this indicates that you got banned by delicious.
 *
 * @author Patrick Meier
 */
public class YouGotBannedException extends DeliciousFeedsException {

    public YouGotBannedException(Throwable cause) {
        super("You may got banned by Delicious. Try sending requests less frequently and " +
                "remember to set a custom user-agent for the requests!", cause);
    }
}
