package scheduler

import (
	"container/heap"
	"testing"
	"time"
)

func TestJobQueuePopsEarliestRunAtFirst(t *testing.T) {
	base := time.Date(2026, 7, 22, 10, 0, 0, 0, time.UTC)
	q := &JobQueue{}
	heap.Init(q)

	heap.Push(q, queueItem{jobId: "third", runAt: base.Add(3 * time.Minute)})
	heap.Push(q, queueItem{jobId: "first", runAt: base.Add(time.Minute)})
	heap.Push(q, queueItem{jobId: "second", runAt: base.Add(2 * time.Minute)})

	for _, wantID := range []string{"first", "second", "third"} {
		got := heap.Pop(q).(queueItem)
		if got.jobId != wantID {
			t.Fatalf("popped job ID = %q, want %q", got.jobId, wantID)
		}
	}
}

func TestJobQueuePeekReturnsEarliestWithoutRemoving(t *testing.T) {
	base := time.Date(2026, 7, 22, 10, 0, 0, 0, time.UTC)
	q := &JobQueue{}
	heap.Init(q)
	heap.Push(q, queueItem{jobId: "later", runAt: base.Add(2 * time.Minute)})
	heap.Push(q, queueItem{jobId: "earlier", runAt: base.Add(time.Minute)})

	jobId, ok := q.Peek()
	if !ok {
		t.Fatal("peek returned ok=false")
	}
	if jobId != "earlier" {
		t.Fatalf("peeked job ID = %q, want earlier", jobId)
	}
	if q.Len() != 2 {
		t.Fatalf("queue length = %d, want 2", q.Len())
	}
}
