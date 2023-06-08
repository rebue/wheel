/**
 * XXX 复制4.3.7版本的io.vertx.httpproxy.impl.CacheControl类的代码
 * 原类不是public的，外部无法访问，未做任何改动
 * <p>
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.httpproxy.impl;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
class CacheControl {

    private int     maxAge;
    private boolean _public;

    public CacheControl parse(String header) {
        maxAge = -1;
        _public = false;
        String[] parts = header.split(","); // No regex
        for (String part : parts) {
            part = part.trim().toLowerCase();
            switch (part) {
                case "public":
                    _public = true;
                    break;
                default:
                    if (part.startsWith("max-age=")) {
                        maxAge = Integer.parseInt(part.substring(8));

                    }
                    break;
            }
        }
        return this;
    }

    public int maxAge() {
        return maxAge;
    }

    public boolean isPublic() {
        return _public;
    }

}
