package com.sensorfields.task;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;

final class LukewarmCompletable extends DisposableCompletableObserver implements CompletableOnSubscribe {

    private boolean complete = false;
    private Throwable error;

    private CompletableEmitter emitter;

    LukewarmCompletable(CompletableSource completable) {
        completable.subscribe(this);
    }

    @Override
    public void subscribe(CompletableEmitter e) throws Exception {
        if (emitter != null) {
            throw new IllegalStateException("Already subscribed to LukewarmCompletable");
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
        if (complete) {
            emitter.onComplete();
        }
        if (error != null) {
            emitter.onError(error);
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

    @Override
    public void onError(Throwable e) {
        if (emitter != null) {
            emitter.onError(e);
        } else {
            error = e;
        }
    }
}
