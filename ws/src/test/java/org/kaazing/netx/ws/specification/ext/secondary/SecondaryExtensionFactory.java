/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.netx.ws.specification.ext.secondary;

import java.io.IOException;

import org.kaazing.netx.ws.internal.ext.WebSocketExtensionFactorySpi;
import org.kaazing.netx.ws.internal.ext.WebSocketExtensionSpi;

public class SecondaryExtensionFactory extends WebSocketExtensionFactorySpi {

    @Override
    public String getExtensionName() {
        return "secondary";
    }

    @Override
    public WebSocketExtensionSpi createExtension(String formattedStr) throws IOException {
        return new SecondaryExtensionSpi();
    }

    @Override
    public void validateExtension(String extensionWithParams) throws IOException {
    }
}
