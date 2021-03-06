/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.leadbank.guardian.ticket;

public abstract class TicketException extends Exception {

    private String code;

    public TicketException(final String code) {
        this.code = code;
    }

    public TicketException(final String code, final Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public final String getCode() {
        return (this.getCause() != null) ? this.getCause().toString()
            : this.code;
    }
}
