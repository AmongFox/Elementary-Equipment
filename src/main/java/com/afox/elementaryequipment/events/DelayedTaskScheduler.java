package com.afox.elementaryequipment.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import java.util.ArrayList;
import java.util.List;

public class DelayedTaskScheduler {
    private static final List<Task> tasks = new ArrayList<>();

    private static class Task {
        int ticksRemaining;
        Runnable action;

        Task(int ticksRemaining, Runnable action) {
            this.ticksRemaining = ticksRemaining;
            this.action = action;
        }
    }

    public static void schedule(int delayTicks, Runnable task) {
        tasks.add(new Task(delayTicks, task));
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<Task> completedTasks = new ArrayList<>();

            for (Task task : tasks) {
                task.ticksRemaining--;
                if (task.ticksRemaining <= 0) {
                    task.action.run();
                    completedTasks.add(task);
                }
            }

            tasks.removeAll(completedTasks);
        });
    }
}