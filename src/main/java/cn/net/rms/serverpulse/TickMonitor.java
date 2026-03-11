package cn.net.rms.serverpulse;

public final class TickMonitor {
    private static final TickMonitor INSTANCE = new TickMonitor();
    private static final int BUFFER_SIZE = 100;
    private static final double NANOS_PER_MS = 1_000_000.0;
    private static final double MAX_TPS = 20.0;
    private static final double MS_PER_SECOND = 1000.0;

    private final long[] tickDurations = new long[BUFFER_SIZE];
    private int index = 0;
    private int count = 0;
    private long tickStartNano;

    private TickMonitor() {}

    public static TickMonitor getInstance() {
        return INSTANCE;
    }

    public void onTickStart() {
        tickStartNano = System.nanoTime();
    }

    public synchronized void onTickEnd() {
        long duration = System.nanoTime() - tickStartNano;
        tickDurations[index] = duration;
        index = (index + 1) % BUFFER_SIZE;
        if (count < BUFFER_SIZE) {
            count++;
        }
    }

    public synchronized double getMspt() {
        if (count == 0) return 0.0;
        long total = 0;
        for (int i = 0; i < count; i++) {
            total += tickDurations[i];
        }
        return (total / (double) count) / NANOS_PER_MS;
    }

    public synchronized double getMsptMin() {
        if (count == 0) return 0.0;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            if (tickDurations[i] < min) {
                min = tickDurations[i];
            }
        }
        return min / NANOS_PER_MS;
    }

    public synchronized double getMsptMax() {
        if (count == 0) return 0.0;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            if (tickDurations[i] > max) {
                max = tickDurations[i];
            }
        }
        return max / NANOS_PER_MS;
    }

    public synchronized double getTps() {
        double mspt = getMspt();
        if (mspt <= 0.0) return MAX_TPS;
        return Math.min(MAX_TPS, MS_PER_SECOND / mspt);
    }
}
