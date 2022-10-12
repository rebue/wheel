/**
 * XXX 复制io.vertx.httpproxy.impl.HttpUtils类的代码，原类会让ctx的后置处理器失效
 *
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.httpproxy.impl;

import java.util.Date;
import java.util.List;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.httpproxy.impl.ParseUtils;

class HttpUtilsEx {

    static Boolean isChunked(MultiMap headers) {
        final List<String> te = headers.getAll("transfer-encoding");
        if (te == null) {
            return false;
        }
        boolean chunked = false;
        for (final String val : te) {
            if (val.equals("chunked")) {
                chunked = true;
            } else {
                return null;
            }
        }
        return chunked;
    }

    static Date dateHeader(MultiMap headers) {
        final String dateHeader = headers.get(HttpHeaders.DATE);
        if (dateHeader != null) {
            return ParseUtils.parseHeaderDate(dateHeader);
        }
        final List<String> warningHeaders = headers.getAll("warning");
        if (warningHeaders.size() > 0) {
            for (final String warningHeader : warningHeaders) {
                final Date date = ParseUtils.parseWarningHeaderDate(warningHeader);
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }
}
