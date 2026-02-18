package Tier0.LocalScheduler.model;

public // Internal Wrapper
class ScheduledTask implements Comparable<ScheduledTask> {
    final Runnable job;
    long executionTime;
    final long period; // 0 for one-time

    public ScheduledTask(Runnable job, long executionTime, long period) {
        this.job = job;
        this.executionTime = executionTime;
        this.period = period;
    }

    @Override
    public int compareTo(ScheduledTask other) {
        return Long.compare(this.executionTime, other.executionTime);
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void reschedule() {
        if (period > 0) {
            this.executionTime += period;
        }
    }

    public boolean isRecurring() {
        return period > 0;
    }

    public Runnable getJob() {
        return job;
    }
}
