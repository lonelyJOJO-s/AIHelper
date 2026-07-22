package store

import (
	"context"
	"errors"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
)

var ErrNotFound = errors.New("job not found")

type Store interface {
	Create(ctx context.Context, j job.Job) error
	Get(ctx context.Context, id string) (job.Job, error)
	List(ctx context.Context) ([]job.Job, error)
	Update(ctx context.Context, j job.Job) error
}
