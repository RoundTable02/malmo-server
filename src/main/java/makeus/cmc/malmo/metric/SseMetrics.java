package makeus.cmc.malmo.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SseMetrics {
    private final AtomicInteger activeSseConnections;

    public SseMetrics(MeterRegistry meterRegistry) {
        this.activeSseConnections = new AtomicInteger(0);

        Gauge.builder("sse.connections.active", activeSseConnections, AtomicInteger::get)
                .description("Number of active SSE connections")
                .register(meterRegistry);
    }

    public void increment() {
        activeSseConnections.incrementAndGet();
    }

    public void decrement() {
        activeSseConnections.decrementAndGet();
    }
}
