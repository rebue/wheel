/**
 * XXX 复制4.3.7版本的io.vertx.httpproxy.impl.HttpUtils类的代码
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

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.httpproxy.impl.ParseUtils;

import java.util.Date;
import java.util.List;

class HttpUtils {

    static Boolean isChunked(MultiMap headers) {
        List<String> te = headers.getAll("transfer-encoding");
        if (te != null) {
            boolean chunked = false;
            for (String val : te) {
                if (val.equals("chunked")) {
                    chunked = true;
                } else {
                    return null;
                }
            }
            return chunked;
        } else {
            return false;
        }
    }

    static Date dateHeader(MultiMap headers) {
        String dateHeader = headers.get(HttpHeaders.DATE);
        if (dateHeader == null) {
            List<String> warningHeaders = headers.getAll("warning");
            if (warningHeaders.size() > 0) {
                for (String warningHeader : warningHeaders) {
                    Date date = ParseUtils.parseWarningHeaderDate(warningHeader);
                    if (date != null) {
                        return date;
                    }
                }
            }
            return null;
        } else {
            return ParseUtils.parseHeaderDate(dateHeader);
        }
    }
}
