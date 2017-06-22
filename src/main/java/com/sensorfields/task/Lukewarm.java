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

    public static <T> MaybeTransformer<T, T> maybe() {
        return upstream -> Maybe.create(new LukewarmMaybe<>(upstream));
    }

    public static <T> FlowableTransformer<T, T> flowable(BackpressureStrategy mode) {
        return upstream -> Flowable.create(new LukewarmFlowable<>(upstream), mode);
    }
}
