package com.sensorfields.task;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.List;

class LukewarmObservable<T> extends DisposableObserver<T> implements ObservableOnSubscribe<T> {

    private final List<T> values = new ArrayList<>();
    private boolean complete = false;
    private Throwable error;

    private ObservableEmitter<T> emitter;

    LukewarmObservable(ObservableSource<T> observable) {
        observable.subscribe(this);
    }

    @Override
    public void subscribe(ObservableEmitter<T> e) throws Exception {
        if (emitter != null) {
            throw new IllegalStateException("Already subscribed to LukewarmObservable");
        }
        emitter = e;
        emitter.setDisposable(new Disposable() {
            @Override
            public void dispose() {
                emitter = null;
            }
            @Override
            public boolean isDisposed() {
                return emitter == null;
            }
        });
        for (T value : values) {
            emitter.onNext(value);
        }
        if (complete) {
            emitter.onComplete();
        }
        if (error != null) {
            emitter.onError(error);
        }
    }

    @Override
    public void onNext(T t) {
        if (emitter != null) {
            emitter.onNext(t);
        } else {
            values.add(t);
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
