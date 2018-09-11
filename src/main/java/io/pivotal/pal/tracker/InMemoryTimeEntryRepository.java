package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    HashMap<Long, TimeEntry> store = new HashMap<>();
    AtomicLong nextId = new AtomicLong(1);
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(nextId.getAndIncrement());
        store.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return store.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(store.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (store.get(id) == null){
            return null;
        }
        timeEntry.setId(id);
        store.put(id, timeEntry);
        return timeEntry;
    }

    public TimeEntry delete(long id) {
        return store.remove(id);
    }
}
