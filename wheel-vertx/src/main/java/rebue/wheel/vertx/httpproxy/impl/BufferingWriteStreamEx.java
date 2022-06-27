/**
 * XXX 复制io.vertx.httpproxy.impl.BufferingWriteStream类的代码，原类不是public的
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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

public class BufferingWriteStreamEx implements WriteStream<Buffer> {

    private final Buffer content;

    public BufferingWriteStreamEx() {
        this.content = Buffer.buffer();
    }

    public Buffer content() {
        return this.content;
    }

    @Override
    public WriteStream<Buffer> exceptionHandler(final Handler<Throwable> handler) {
        return this;
    }

    @Override
    public Future<Void> write(final Buffer data) {
        this.content.appendBuffer(data);
        return Future.succeededFuture();
    }

    @Override
    public void write(final Buffer data, final Handler<AsyncResult<Void>> handler) {
        this.content.appendBuffer(data);
        handler.handle(Future.succeededFuture());
    }

    @Override
    public void end(final Handler<AsyncResult<Void>> handler) {
        handler.handle(Future.succeededFuture());
    }

    @Override
    public WriteStream<Buffer> setWriteQueueMaxSize(final int maxSize) {
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return false;
    }

    @Override
    public WriteStream<Buffer> drainHandler(final Handler<Void> handler) {
        return this;
    }
}
