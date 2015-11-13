package io.reactivex.rxcassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class SessionObservableTest {

    @Test
    public void testName() throws Exception {
        // boot cassandra with cassandra unit

        Cluster cluster = Cluster.builder()
                .addContactPoint("127.0.0.1")
                .build();

        Session session = cluster.connect("demo");

        SessionObservable sessionObs = new SessionObservable(session);

        CountDownLatch latch = new CountDownLatch(1);
        sessionObs.prepare("SELECT data FROM demo_table LIMIT 100")
                .toObservable() // return Observable instead of Single ?
                .flatMap(ps -> sessionObs.execute(ps.bind()))
                .subscribe(r -> latch.countDown());

        latch.await();
        cluster.close();

    }
}