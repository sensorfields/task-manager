package com.sensorfields.task;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class LukewarmObservableTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private Subject<String> source;
    private Observable<String> lukewarmObservable;

    @Before
    public void before() {
        source = PublishSubject.create();
        lukewarmObservable = source.compose(Lukewarm.observable());
    }

    @Test
    public void subscribeTwice_throwsException() {
        lukewarmObservable.test();
        lukewarmObservable.test().assertError(IllegalStateException.class);
    }

    @Test
    public void compose_hasSubscribers() {
        assertTrue(source.hasObservers());
    }

    @Test
    public void onNextBeforeSubscribe() {
        source.onNext("ONE");

        TestObserver<String> observer = lukewarmObservable.test();
        observer.assertValues("ONE");
        observer.assertNotTerminated();
    }

    @Test
    public void onCompleteBeforeSubscribe() {
        source.onComplete();

        TestObserver<String> observer = lukewarmObservable.test();
        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorBeforeSubscribe() {
        source.onError(new IllegalArgumentException());

        TestObserver<String> observer = lukewarmObservable.test();
        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextAfterSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();

        source.onNext("ONE");

        observer.assertValues("ONE");
        observer.assertNotTerminated();
    }

    @Test
    public void onCompleteAfterSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();

        source.onComplete();

        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorAfterSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();

        source.onError(new IllegalArgumentException());

        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextBeforeAndAfterSubscribe() {
        source.onNext("ONE");

        TestObserver<String> observer = lukewarmObservable.test();

        source.onNext("TWO");

        observer.assertValues("ONE", "TWO");
        observer.assertNotTerminated();
    }

    @Test
    public void onNextAndCompleteBeforeSubscribe() {
        source.onNext("ONE");
        source.onComplete();

        TestObserver<String> observer = lukewarmObservable.test();

        observer.assertValues("ONE");
        observer.assertComplete();
    }

    @Test
    public void onNextAndErrorBeforeSubscribe() {
        source.onNext("ONE");
        source.onError(new IllegalArgumentException());

        TestObserver<String> observer = lukewarmObservable.test();

        observer.assertValues("ONE");
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextWhenReSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();
        observer.dispose();

        source.onNext("ONE");

        TestObserver<String> observer2 = lukewarmObservable.test();

        observer2.assertValues("ONE");
        observer2.assertNotTerminated();
    }

    @Test
    public void onCompleteWhenReSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();
        observer.dispose();

        source.onComplete();

        TestObserver<String> observer2 = lukewarmObservable.test();

        observer2.assertNoValues();
        observer2.assertComplete();
    }

    @Test
    public void onErrorWhenReSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();
        observer.dispose();

        source.onError(new IllegalArgumentException());

        TestObserver<String> observer2 = lukewarmObservable.test();

        observer2.assertNoValues();
        observer2.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextAndCompleteWhenReSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();

        source.onNext("ONE");
        observer.assertValues("ONE");

        observer.dispose();

        source.onNext("TWO");
        source.onNext("THREE");
        source.onComplete();

        TestObserver<String> observer2 = lukewarmObservable.test();

        observer2.assertValues("TWO", "THREE");
        observer2.assertComplete();
    }

    @Test
    public void onNextAndErrorWhenReSubscribe() {
        TestObserver<String> observer = lukewarmObservable.test();

        source.onNext("ONE");
        observer.assertValues("ONE");

        observer.dispose();

        source.onNext("TWO");
        source.onNext("THREE");
        source.onError(new IllegalArgumentException());

        TestObserver<String> observer2 = lukewarmObservable.test();

        observer2.assertValues("TWO", "THREE");
        observer2.assertError(IllegalArgumentException.class);
    }
}
