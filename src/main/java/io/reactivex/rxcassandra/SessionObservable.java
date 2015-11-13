package io.reactivex.rxcassandra;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.exceptions.OnErrorThrowable;

// extract interface ? (session<ResultSet>, Session<Row> ?)
public class SessionObservable {
    private final Session session;

    public SessionObservable(Session session) {
        this.session = session;
    }

    public Observable<ResultSet> execute(Statement statement) {
        return Observable.defer(() -> {
            ListenableFuture<ResultSet> future = session.executeAsync(statement);
            return Observable.create(new ToObservable<>(future));
        });
    }

    public Observable<ResultSet> execute(String var1) {
        return Observable.error(new RuntimeException("NOT IMPLEMENTED"));
    }

    public Observable<ResultSet> execute(String var1, Object... var2) {
        return Observable.error(new RuntimeException("NOT IMPLEMENTED"));
    }


    public Observable<Void> close() {
        return Observable.error(new RuntimeException("NOT IMPLEMENTED"));
    }

    public Single<PreparedStatement> prepare(String cql) {
        return Single.defer(() -> {
            ListenableFuture<PreparedStatement> future = session.prepareAsync(cql);
            return Single.create(new ToSingle<>(future));
        });
    }

    public Single<PreparedStatement> prepare(RegularStatement statement) {
        return prepare(statement.getQueryString());
    }


    private static class ToSingle<T> implements Single.OnSubscribe<T> {

        private final ListenableFuture<T> parent;

        private ToSingle(ListenableFuture<T> parent) {
            this.parent = parent;
        }

        @Override
        public void call(SingleSubscriber<? super T> singleSubscriber) {
            Futures.addCallback(parent, new FutureCallback<T>() {
                @Override
                public void onSuccess(T result) {
                    singleSubscriber.onSuccess(result);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    singleSubscriber.onError(OnErrorThrowable.from(throwable));
                }
            });
        }
    }

    private static class ToObservable<T> implements Observable.OnSubscribe<T> {
        private final ListenableFuture<T> parent;

        private ToObservable(ListenableFuture<T> parent) {
            this.parent = parent;
        }

        @Override
        public void call(Subscriber<? super T> subscriber) {
            // // TODO: 13/11/15 BACKPRESSURE
            Futures.addCallback(parent, new FutureCallback<T>() {
                @Override
                public void onSuccess(T result) {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    subscriber.onError(OnErrorThrowable.from(throwable));
                }
            });
        }
    }

}
