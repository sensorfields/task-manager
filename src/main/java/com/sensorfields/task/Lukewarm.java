package com.sensorfields.task;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public final class Lukewarm {

    private Lukewarm() {}

    public static <T> ObservableTransformer<T, T> observable() {
        return upstream -> Observable.create(new LukewarmObservable<>(upstream));
    }

    public static CompletableTransformer completable() {
        return upstream -> Completable.create(new LukewarmCompletable(upstream));
    }
}
