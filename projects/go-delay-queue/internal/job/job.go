package job

import (
	"encoding/json"
	"errors"
	"strings"
	"time"
)

type Status string

const (
	StatusPending   Status = "pending"
	StatusRunning   Status = "running"
	StatusSucceeded Status = "succeeded"
	StatusFailed    Status = "failed"
	StatusCanceled  Status = "canceled"
)

var (
	ErrBlankName     = errors.New("job name must not be blank")
	ErrRunAtRequired = errors.New("runAt must be provided")
)

type Job struct {
	ID         string          `json:"id"`
	Name       string          `json:"name"`
	Payload    json.RawMessage `json:"payload,omitempty"`
	RunAt      time.Time       `json:"runAt"`
	MaxRetries int             `json:"maxRetries"`
	Attempts   int             `json:"attempts"`
	Status     Status          `json:"status"`
	LastError  string          `json:"lastError,omitempty"`
	CreatedAt  time.Time       `json:"createdAt"`
	UpdatedAt  time.Time       `json:"updatedAt"`
}

type SubmitRequest struct {
	Name       string          `json:"name"`
	Payload    json.RawMessage `json:"payload,omitempty"`
	RunAt      time.Time       `json:"runAt"`
	MaxRetries int             `json:"maxRetries"`
}

func (r SubmitRequest) Validate() error {
	if strings.TrimSpace(r.Name) == "" {
		return ErrBlankName
	}
	if r.RunAt.IsZero() {
		return ErrRunAtRequired
	}
	return nil
}

func New(id string, req SubmitRequest, now time.Time) Job {
	return Job{
		ID:         id,
		Name:       strings.TrimSpace(req.Name),
		Payload:    req.Payload,
		RunAt:      req.RunAt,
		MaxRetries: req.MaxRetries,
		Status:     StatusPending,
		CreatedAt:  now,
		UpdatedAt:  now,
	}
}

func (job Job) IsTerminal() bool {
	return job.Status == StatusSucceeded ||
		job.Status == StatusFailed ||
		job.Status == StatusCanceled
}
