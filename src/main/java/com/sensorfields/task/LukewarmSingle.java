package com.sensorfields.task;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

final class LukewarmSingle<T> extends DisposableSingleObserver<T> implements SingleOnSubscribe<T> {

    private T value;
    private Throwable error;

    private SingleEmitter<T> emitter;

    LukewarmSingle(SingleSource<T> source) {
        source.subscribe(this);
    }

    @Override
    public void subscribe(SingleEmitter<T> e) throws Exception {
        if (emitter != null) {
            throw new IllegalStateException("Already subscribed to LukewarmSingle");
        }
        emitter = e;
        emitter.setDisposable(new Disposable() {
            @Override
            public void dispose() {
                emitter.setDisposable(null);
                emitter = null;
            }
            @Override
            public boolean isDisposed() {
                return emitter == null;
            }
        });
        if (value != null) {
            emitter.onSuccess(value);
        }
        if (error != null) {
            emitter.onError(error);
        }
    }

    @Override
    public void onSuccess(T t) {
        if (emitter != null) {
            emitter.onSuccess(t);
        } else {
            value = t;
        }
    }

    @Override
    public void onError(Throwable e) {
        if (emitter != null) {
            emitter.onError(e);
        } else {
            error = e;
        }
    }
}
