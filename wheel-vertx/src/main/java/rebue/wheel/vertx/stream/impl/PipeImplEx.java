/**
 * XXX 复制4.3.7版本的io.vertx.core.streams.impl.PipeImpl，加上一些日志打印方便调试
 *
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.stream.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.VertxException;
import io.vertx.core.streams.Pipe;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeImplEx<T> implements Pipe<T> {

    private final Promise<Void> result;
    private final ReadStream<T> src;
    private boolean endOnSuccess = true;
    private boolean endOnFailure = true;
    private WriteStream<T> dst;

    public PipeImplEx(ReadStream<T> src) {
        this.src = src;
        this.result = Promise.promise();

        // Set handlers now
        src.endHandler(result::tryComplete);
        src.exceptionHandler(result::tryFail);
    }

    @Override
    public synchronized Pipe<T> endOnFailure(boolean end) {
        endOnFailure = end;
        return this;
    }

    @Override
    public synchronized Pipe<T> endOnSuccess(boolean end) {
        endOnSuccess = end;
        return this;
    }

    @Override
    public synchronized Pipe<T> endOnComplete(boolean end) {
        endOnSuccess = end;
        endOnFailure = end;
        return this;
    }

    private void handleWriteResult(AsyncResult<Void> ack) {
        if (ack.failed()) {
            result.tryFail(new WriteException(ack.cause()));
        }
    }

    @Override
    public void to(WriteStream<T> ws, Handler<AsyncResult<Void>> completionHandler) {
        log.debug("PipeImplEx.to");
        if (ws == null) {
            throw new NullPointerException();
        }
        @SuppressWarnings("unused")
        boolean endOnSuccess;
        @SuppressWarnings("unused")
        boolean endOnFailure;
        synchronized (PipeImplEx.this) {
            if (dst != null) {
                throw new IllegalStateException();
            }
            dst = ws;
            endOnSuccess = this.endOnSuccess;
            endOnFailure = this.endOnFailure;
        }
        Handler<Void> drainHandler = v -> src.resume();
        src.handler(item -> {
            log.debug("PipeImplEx.to.src.handler");
            ws.write(item, this::handleWriteResult);
            if (ws.writeQueueFull()) {
                src.pause();
                ws.drainHandler(drainHandler);
            }
        });
        src.resume();
        result.future().onComplete(ar -> {
            log.debug("PipeImplEx.to.result.future.onComplete");
            try {
                src.handler(null);
            } catch (Exception ignore) {
            }
            try {
                src.exceptionHandler(null);
            } catch (Exception ignore) {
            }
            try {
                src.endHandler(null);
            } catch (Exception ignore) {
            }
            if (ar.succeeded()) {
                log.debug("PipeImplEx.to.result.future.onComplete: ar.succeeded");
                handleSuccess(completionHandler);
            } else {
                Throwable err = ar.cause();
                log.error("PipeImplEx.to.result.future.onComplete: ar.err-{}", err);
                if (err instanceof WriteException) {
                    src.resume();
                    err = err.getCause();
                }
                handleFailure(err, completionHandler);
            }
        });
    }

    private void handleSuccess(Handler<AsyncResult<Void>> completionHandler) {
        if (endOnSuccess) {
            dst.end(completionHandler);
        } else {
            completionHandler.handle(Future.succeededFuture());
        }
    }

    private void handleFailure(Throwable cause, Handler<AsyncResult<Void>> completionHandler) {
        Future<Void> res = Future.failedFuture(cause);
        if (endOnFailure){
            dst.end(ignore -> {
                completionHandler.handle(res);
            });
        } else {
            completionHandler.handle(res);
        }
    }

    public void close() {
        synchronized (this) {
            src.exceptionHandler(null);
            src.handler(null);
            if (dst != null) {
                dst.drainHandler(null);
                dst.exceptionHandler(null);
            }
        }
        VertxException err = new VertxException("Pipe closed", true);
        if (result.tryFail(err)) {
            src.resume();
        }
    }

    private static class WriteException extends VertxException {
        private WriteException(Throwable cause) {
            super(cause, true);
        }
    }
}
