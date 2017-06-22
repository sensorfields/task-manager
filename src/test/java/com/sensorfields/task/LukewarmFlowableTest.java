package com.sensorfields.task;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class LukewarmFlowableTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private PublishProcessor<String> source;
    private Flowable<String> lukewarmFlowable;

    @Before
    public void before() {
        source = PublishProcessor.create();
        lukewarmFlowable = source.compose(Lukewarm.flowable(BackpressureStrategy.MISSING));
    }

    @Test
    public void subscribeTwice_throwsException() {
        lukewarmFlowable.test();
        lukewarmFlowable.test().assertError(IllegalStateException.class);
    }

    @Test
    public void compose_hasSubscribers() {
        assertTrue(source.hasSubscribers());
    }

    @Test
    public void onNextBeforeSubscribe() {
        source.onNext("ONE");

        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.assertValues("ONE");
        observer.assertNotTerminated();
    }

    @Test
    public void onCompleteBeforeSubscribe() {
        source.onComplete();

        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorBeforeSubscribe() {
        source.onError(new IllegalArgumentException());

        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextAfterSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onNext("ONE");

        observer.assertValues("ONE");
        observer.assertNotTerminated();
    }

    @Test
    public void onCompleteAfterSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onComplete();

        observer.assertNoValues();
        observer.assertComplete();
    }

    @Test
    public void onErrorAfterSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onError(new IllegalArgumentException());

        observer.assertNoValues();
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextBeforeAndAfterSubscribe() {
        source.onNext("ONE");

        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onNext("TWO");

        observer.assertValues("ONE", "TWO");
        observer.assertNotTerminated();
    }

    @Test
    public void onNextAndCompleteBeforeSubscribe() {
        source.onNext("ONE");
        source.onComplete();

        TestSubscriber<String> observer = lukewarmFlowable.test();

        observer.assertValues("ONE");
        observer.assertComplete();
    }

    @Test
    public void onNextAndErrorBeforeSubscribe() {
        source.onNext("ONE");
        source.onError(new IllegalArgumentException());

        TestSubscriber<String> observer = lukewarmFlowable.test();

        observer.assertValues("ONE");
        observer.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextWhenReSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.dispose();

        source.onNext("ONE");

        TestSubscriber<String> observer2 = lukewarmFlowable.test();

        observer2.assertValues("ONE");
        observer2.assertNotTerminated();
    }

    @Test
    public void onCompleteWhenReSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.dispose();

        source.onComplete();

        TestSubscriber<String> observer2 = lukewarmFlowable.test();

        observer2.assertNoValues();
        observer2.assertComplete();
    }

    @Test
    public void onErrorWhenReSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();
        observer.dispose();

        source.onError(new IllegalArgumentException());

        TestSubscriber<String> observer2 = lukewarmFlowable.test();

        observer2.assertNoValues();
        observer2.assertError(IllegalArgumentException.class);
    }

    @Test
    public void onNextAndCompleteWhenReSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onNext("ONE");
        observer.assertValues("ONE");

        observer.dispose();

        source.onNext("TWO");
        source.onNext("THREE");
        source.onComplete();

        TestSubscriber<String> observer2 = lukewarmFlowable.test();

        observer2.assertValues("TWO", "THREE");
        observer2.assertComplete();
    }

    @Test
    public void onNextAndErrorWhenReSubscribe() {
        TestSubscriber<String> observer = lukewarmFlowable.test();

        source.onNext("ONE");
        observer.assertValues("ONE");

        observer.dispose();

        source.onNext("TWO");
        source.onNext("THREE");
        source.onError(new IllegalArgumentException());

        TestSubscriber<String> observer2 = lukewarmFlowable.test();

        observer2.assertValues("TWO", "THREE");
        observer2.assertError(IllegalArgumentException.class);
    }
}
