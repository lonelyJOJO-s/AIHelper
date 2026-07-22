package scheduler

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/store"
)

func TestCancelMarksPendingJobCanceled(t *testing.T) {
	ctx := context.Background()
	st := store.NewMemoryStore()
	s := New(st)
	createdAt := time.Date(2026, 7, 22, 8, 0, 0, 0, time.UTC)
	canceledAt := createdAt.Add(time.Minute)
	s.now = func() time.Time {
		return canceledAt
	}
	j := job.Job{
		ID:        "job-1",
		Name:      "demo",
		RunAt:     createdAt.Add(time.Hour),
		Status:    job.StatusPending,
		CreatedAt: createdAt,
		UpdatedAt: createdAt,
	}
	if err := st.Create(ctx, j); err != nil {
		t.Fatalf("create job: %v", err)
	}

	canceled, err := s.Cancel(ctx, j.ID)
	if err != nil {
		t.Fatalf("cancel job: %v", err)
	}

	if canceled.Status != job.StatusCanceled {
		t.Fatalf("status = %q, want %q", canceled.Status, job.StatusCanceled)
	}
	if !canceled.UpdatedAt.Equal(canceledAt) {
		t.Fatalf("updatedAt = %s, want %s", canceled.UpdatedAt, canceledAt)
	}
}

func TestCancelRejectsTerminalJob(t *testing.T) {
	ctx := context.Background()
	st := store.NewMemoryStore()
	s := New(st)
	j := job.Job{
		ID:     "job-2",
		Name:   "demo",
		RunAt:  time.Now().Add(time.Hour),
		Status: job.StatusSucceeded,
	}
	if err := st.Create(ctx, j); err != nil {
		t.Fatalf("create job: %v", err)
	}

	_, err := s.Cancel(ctx, j.ID)
	if !errors.Is(err, ErrTerminalJob) {
		t.Fatalf("error = %v, want %v", err, ErrTerminalJob)
	}
}
