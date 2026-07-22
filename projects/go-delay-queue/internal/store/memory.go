package store

import (
	"context"
	"sync"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
)

type MemoryStore struct {
	mu   sync.RWMutex
	jobs map[string]job.Job
}

func NewMemoryStore() *MemoryStore {
	return &MemoryStore{
		jobs: make(map[string]job.Job),
	}
}

func (s *MemoryStore) Create(_ context.Context, j job.Job) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	s.jobs[j.ID] = j
	return nil
}

func (s *MemoryStore) Get(_ context.Context, id string) (job.Job, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	j, ok := s.jobs[id]
	if !ok {
		return job.Job{}, ErrNotFound
	}
	return j, nil
}

func (s *MemoryStore) List(_ context.Context) ([]job.Job, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	jobs := make([]job.Job, 0, len(s.jobs))
	for _, j := range s.jobs {
		jobs = append(jobs, j)
	}
	return jobs, nil
}

func (s *MemoryStore) Update(_ context.Context, j job.Job) error {
	s.mu.Lock()
	defer s.mu.Unlock()

	if _, ok := s.jobs[j.ID]; !ok {
		return ErrNotFound
	}
	s.jobs[j.ID] = j
	return nil
}
