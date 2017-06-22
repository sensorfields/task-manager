package com.sensorfields.task;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public final class Lukewarm {

    private Lukewarm() {}

    public static <T> ObservableTransformer<T, T> observable() {
        return upstream -> Observable.create(new LukewarmObservable<>(upstream));
    }
}
