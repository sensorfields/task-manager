package com.sensorfields.task;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DisposableSubscriber;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

class LukewarmFlowable<T> extends DisposableSubscriber<T> implements FlowableOnSubscribe<T> {

    private final List<T> values = new ArrayList<>();
    private boolean complete = false;
    private Throwable error;

    private FlowableEmitter<T> emitter;

    LukewarmFlowable(Publisher<T> source) {
        source.subscribe(this);
    }

    @Override
    public void subscribe(FlowableEmitter<T> e) throws Exception {
        if (emitter != null) {
            throw new IllegalStateException("Already subscribed to LukewarmFlowable");
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
