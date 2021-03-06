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
 * An Exception which wraps other exceptions that may occur. So you only have to catch this one.
 *
 * @author Patrick Meier
 */
public class DeliciousFeedsException extends RuntimeException {

    public DeliciousFeedsException(String message) {
        super(message);
    }

    public DeliciousFeedsException(String message, Throwable cause) {
        super(message, cause);
    }
}
