package scheduler

import (
	"context"
	"errors"
	"fmt"
	"sync/atomic"
	"time"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/store"
)

var ErrNotImplemented = errors.New("scheduler core is not implemented yet")

var ErrTerminalJob = errors.New("job is already terminal")

type Scheduler struct {
	store   store.Store
	now     func() time.Time
	counter atomic.Uint64
}

func New(st store.Store) *Scheduler {
	return &Scheduler{
		store: st,
		now:   time.Now,
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
	return j, nil
}

func (s *Scheduler) Start(ctx context.Context) error {
	<-ctx.Done()
	return ctx.Err()
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
	j.UpdatedAt = s.now().UTC()
	if err := s.store.Update(ctx, j); err != nil {
		return job.Job{}, err
	}
	return j, nil
}

func (s *Scheduler) nextID(now time.Time) string {
	return fmt.Sprintf("job_%d_%06d", now.UnixNano(), s.counter.Add(1))
}
