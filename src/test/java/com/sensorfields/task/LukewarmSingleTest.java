package com.sensorfields.task;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.SingleSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class LukewarmSingleTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private SingleSubject<String> source;
    private Single<String> lukewarmSingle;

    @Before
    public void before() {
        source = SingleSubject.create();
        lukewarmSingle = source.compose(Lukewarm.single());
    }

    @Test
    public void subscribeTwice_throwsException() {
        lukewarmSingle.test();
        lukewarmSingle.test().assertError(IllegalStateException.class);
    }

    @Test
    public void compose_hasSubscribers() {
        assertTrue(source.hasObservers());
    }

    @Test
    public void onSuccessBeforeSubscribe() {
        source.onSuccess("ONE");

        TestObserver<String> observer = lukewarmSingle.test();
        assertSuccess(observer, "ONE");
    }

    @Test
    public void onErrorBeforeSubscribe() {
        source.onError(new IllegalArgumentException());

        TestObserver<String> observer = lukewarmSingle.test();
        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onSuccessAfterSubscribe() {
        TestObserver<String> observer = lukewarmSingle.test();

        source.onSuccess("ONE");

        assertSuccess(observer, "ONE");
    }

    @Test
    public void onErrorAfterSubscribe() {
        TestObserver<String> observer = lukewarmSingle.test();

        source.onError(new IllegalArgumentException());

        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onSuccessWhenReSubscribe() {
        TestObserver<String> observer = lukewarmSingle.test();
        observer.dispose();

        source.onSuccess("ONE");

        TestObserver<String> observer2 = lukewarmSingle.test();

        assertSuccess(observer2, "ONE");
    }

    @Test
    public void onErrorWhenReSubscribe() {
        TestObserver<String> observer = lukewarmSingle.test();
        observer.dispose();

        source.onError(new IllegalArgumentException());

        TestObserver<String> observer2 = lukewarmSingle.test();

        observer2.assertNoValues();
        observer2.assertError(IllegalArgumentException.class);
    }

    private void assertSuccess(TestObserver<String> observer, String value) {
        observer.assertValues(value);
        observer.assertComplete();
    }
}
