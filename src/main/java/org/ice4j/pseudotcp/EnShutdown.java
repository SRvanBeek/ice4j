/*
 * ice4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
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
package org.ice4j.pseudotcp;

/**
 * <tt>EnShutdown</tt> enumeration used internally
 *
 * @author Pawel Domas
 */
public enum EnShutdown
{
    /**
     * There was no shutdown
     */
    SD_NONE,
    /**
     * There was a graceful shutdown
     */
    SD_GRACEFUL,
    /**
     * There was a forceful shutdown
     */
    SD_FORCEFUL
}
