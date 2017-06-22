package com.sensorfields.task;

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.MaybeSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class LukewarmMaybeTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private MaybeSubject<String> source;
    private Maybe<String> lukewarmMaybe;

    @Before
    public void before() {
        source = MaybeSubject.create();
        lukewarmMaybe = source.compose(Lukewarm.maybe());
    }

    @Test
    public void subscribeTwice_throwsException() {
        lukewarmMaybe.test();
        lukewarmMaybe.test().assertError(IllegalStateException.class);
    }

    @Test
    public void compose_hasSubscribers() {
        assertTrue(source.hasObservers());
    }

    @Test
    public void onSuccessBeforeSubscribe() {
        source.onSuccess("ONE");

        TestObserver<String> observer = lukewarmMaybe.test();
        assertSuccess(observer, "ONE");
    }

    @Test
    public void onCompleteBeforeSubscribe() {
        source.onComplete();

        TestObserver<String> observer = lukewarmMaybe.test();
        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorBeforeSubscribe() {
        source.onError(new IllegalArgumentException());

        TestObserver<String> observer = lukewarmMaybe.test();
        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onSuccessAfterSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();

        source.onSuccess("ONE");

        assertSuccess(observer, "ONE");
    }

    @Test
    public void onCompleteAfterSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();

        source.onComplete();

        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorAfterSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();

        source.onError(new IllegalArgumentException());

        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onSuccessWhenReSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();
        observer.dispose();

        source.onSuccess("ONE");

        TestObserver<String> observer2 = lukewarmMaybe.test();

        assertSuccess(observer2, "ONE");
    }

    @Test
    public void onCompleteWhenReSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();
        observer.dispose();

        source.onComplete();

        TestObserver<String> observer2 = lukewarmMaybe.test();

        observer2.assertNoValues();
        observer2.assertComplete();
    }

    @Test
    public void onErrorWhenReSubscribe() {
        TestObserver<String> observer = lukewarmMaybe.test();
        observer.dispose();

        source.onError(new IllegalArgumentException());

        TestObserver<String> observer2 = lukewarmMaybe.test();

        observer2.assertNoValues();
        observer2.assertError(IllegalArgumentException.class);
    }

    private void assertSuccess(TestObserver<String> observer, String value) {
        observer.assertValues(value);
        observer.assertComplete();
    }
}
