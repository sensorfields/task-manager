package com.sensorfields.task;

import io.reactivex.*;

public final class Lukewarm {

    private Lukewarm() {}

    public static <T> ObservableTransformer<T, T> observable() {
        return upstream -> Observable.create(new LukewarmObservable<>(upstream));
    }

    public static CompletableTransformer completable() {
        return upstream -> Completable.create(new LukewarmCompletable(upstream));
    }

    public static <T> SingleTransformer<T, T> single() {
        return upstream -> Single.create(new LukewarmSingle<>(upstream));
    }
}
