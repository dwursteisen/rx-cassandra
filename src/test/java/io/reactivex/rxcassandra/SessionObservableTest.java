package io.reactivex.rxcassandra;

import org.junit.Test;

public class SessionObservableTest {

    @Test
    public void testName() throws Exception {
        // boot cassandra with cassandra unit

        SessionObservable session = new SessionObservable(null);

        session.prepare("SELECT....")
                .toObservable() // return Observable instead of Single ?
                .flatMap(ps -> session.execute(ps.bind()))
                .subscribe();

    }
}