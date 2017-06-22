package com.sensorfields.task;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.CompletableSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class LukewarmCompletableTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private CompletableSubject source;
    private Completable lukewarmCompletable;

    @Before
    public void before() {
        source = CompletableSubject.create();
        lukewarmCompletable = source.compose(Lukewarm.completable());
    }

    @Test
    public void subscribeTwice_throwsException() {
        lukewarmCompletable.test();
        lukewarmCompletable.test().assertError(IllegalStateException.class);
    }

    @Test
    public void compose_hasSubscribers() {
        assertTrue(source.hasObservers());
    }

    @Test
    public void onCompleteBeforeSubscribe() {
        source.onComplete();

        TestObserver<Void> observer = lukewarmCompletable.test();
        observer.assertComplete();
    }

    @Test
    public void onErrorBeforeSubscribe() {
        source.onError(new IllegalArgumentException());

        TestObserver<Void> observer = lukewarmCompletable.test();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onCompleteAfterSubscribe() {
        TestObserver<Void> observer = lukewarmCompletable.test();

        source.onComplete();

        observer.assertComplete();
    }

    @Test
    public void onErrorAfterSubscribe() {
        TestObserver<Void> observer = lukewarmCompletable.test();

        source.onError(new IllegalArgumentException());

        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onCompleteWhenReSubscribe() {
        TestObserver<Void> observer = lukewarmCompletable.test();
        observer.dispose();

        source.onComplete();

        TestObserver<Void> observer2 = lukewarmCompletable.test();

        observer2.assertComplete();
    }

    @Test
    public void onErrorWhenReSubscribe() {
        TestObserver<Void> observer = lukewarmCompletable.test();
        observer.dispose();

        source.onError(new IllegalArgumentException());

        TestObserver<Void> observer2 = lukewarmCompletable.test();

        observer2.assertError(IllegalArgumentException.class);
    }
}
