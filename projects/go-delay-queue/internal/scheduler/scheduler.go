package scheduler

import (
	"container/heap"
	"context"
	"errors"
	"fmt"
	"log/slog"
	"sync"
	"sync/atomic"
	"time"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/store"
)

var ErrNotImplemented = errors.New("scheduler core is not implemented yet")

var ErrTerminalJob = errors.New("job is already terminal")

type Scheduler struct {
	store    store.Store
	now      func() time.Time
	counter  atomic.Uint64
	JobQueue *JobQueue
	mu       sync.Mutex
}

func New(st store.Store) *Scheduler {
	return &Scheduler{
		store:    st,
		now:      time.Now,
		JobQueue: NewJobQueue(),
	}
}

func (s *Scheduler) Submit(ctx context.Context, req job.SubmitRequest) (job.Job, error) {
	if err := req.Validate(); err != nil {
		return job.Job{}, err
	}
	now := s.now().UTC()
	j := job.New(s.nextID(now), req, now)
	if err := s.store.Create(ctx, j); err != nil {
		return job.Job{}, err
	}
	s.mu.Lock()
	defer s.mu.Unlock()
	heap.Push(s.JobQueue, queueItem{
		jobId: j.ID,
		runAt: j.RunAt,
	})
	return j, nil
}

func (s *Scheduler) Start(ctx context.Context) error {
	ticker := time.NewTicker(time.Millisecond * 50)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			s.handle(ctx)
		case <-ctx.Done():
			return ctx.Err()
		}
	}

}

func (s *Scheduler) handle(ctx context.Context) {
	s.mu.Lock()
	defer s.mu.Unlock()
	for {
		topId, ok := s.JobQueue.Peek()
		if !ok {
			return
		}
		j, err := s.store.Get(ctx, topId)
		if err != nil {
			slog.ErrorContext(ctx, "get job error",
				"job_id", topId,
				"error", err)
			heap.Pop(s.JobQueue)
			continue
		}
		if j.RunAt.After(s.now().UTC()) {
			break
		}
		j.Status = job.StatusRunning
		if err := s.store.Update(ctx, j); err != nil {
			slog.ErrorContext(ctx, "mark job running status failed",
				"job_id", j.ID,
				"error", err,
			)
			heap.Pop(s.JobQueue)
			continue
		}
		// todo: execute
		success := true
		if success {
			j.Status = job.StatusSucceeded
		} else {
			j.Status = job.StatusFailed
		}
		if err := s.store.Update(ctx, j); err != nil {
			slog.ErrorContext(ctx, "mark job execute status failed",
				"job_id", j.ID,
				"error", err,
			)
		}
		heap.Pop(s.JobQueue)
	}
}

func (s *Scheduler) Cancel(ctx context.Context, id string) (job.Job, error) {
	j, err := s.store.Get(ctx, id)
	if err != nil {
		return job.Job{}, err
	}
	if j.IsTerminal() {
		return job.Job{}, ErrTerminalJob
	}
	j.Status = job.StatusCanceled
	if err := s.store.Update(ctx, j); err != nil {
		return job.Job{}, err
	}
	return s.store.Get(ctx, id)
}

func (s *Scheduler) nextID(now time.Time) string {
	return fmt.Sprintf("job_%d_%06d", now.UnixNano(), s.counter.Add(1))
}
