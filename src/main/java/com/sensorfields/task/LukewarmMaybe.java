package com.sensorfields.task;

import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;

final class LukewarmMaybe<T> extends DisposableMaybeObserver<T> implements MaybeOnSubscribe<T> {

    private T value;
    private boolean complete;
    private Throwable error;

    private MaybeEmitter<T> emitter;

    LukewarmMaybe(MaybeSource<T> source) {
        source.subscribe(this);
    }

    @Override
    public void subscribe(MaybeEmitter<T> e) throws Exception {
        if (emitter != null) {
            throw new IllegalStateException("Already subscribed to LukewarmMaybe");
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
        if (complete) {
            emitter.onComplete();
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

    @Override
    public void onComplete() {
        if (emitter != null) {
            emitter.onComplete();
        } else {
            complete = true;
        }
    }
}
