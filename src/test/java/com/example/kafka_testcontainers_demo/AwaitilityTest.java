package com.example.kafka_testcontainers_demo;
import static org.awaitility.Awaitility.*;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
public class AwaitilityTest {
    @Test
    public void testAsyncOperation() {
        String[] result = {null};
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                result[0] = "done";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        await().atMost(5, TimeUnit.SECONDS).until(() -> result[0] != null);
    }
}
