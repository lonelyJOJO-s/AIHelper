package api

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/job"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/scheduler"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/store"
)

type Server struct {
	store     store.Store
	scheduler *scheduler.Scheduler
}

func NewServer(st store.Store, sched *scheduler.Scheduler) *Server {
	return &Server{
		store:     st,
		scheduler: sched,
	}
}

func (s *Server) Handler() http.Handler {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", s.health)
	mux.HandleFunc("POST /jobs", s.submitJob)
	mux.HandleFunc("GET /jobs", s.listJobs)
	mux.HandleFunc("GET /jobs/{id}", s.getJob)
	mux.HandleFunc("POST /jobs/{id}/cancel", s.cancelJob)
	return mux
}

func (s *Server) health(w http.ResponseWriter, _ *http.Request) {
	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}

func (s *Server) submitJob(w http.ResponseWriter, r *http.Request) {
	var req job.SubmitRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		writeError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	j, err := s.scheduler.Submit(r.Context(), req)
	if err != nil {
		writeError(w, http.StatusBadRequest, err.Error())
		return
	}
	writeJSON(w, http.StatusCreated, j)
}

func (s *Server) listJobs(w http.ResponseWriter, r *http.Request) {
	jobs, err := s.store.List(r.Context())
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, jobs)
}

func (s *Server) getJob(w http.ResponseWriter, r *http.Request) {
	j, err := s.store.Get(r.Context(), r.PathValue("id"))
	if errors.Is(err, store.ErrNotFound) {
		writeError(w, http.StatusNotFound, err.Error())
		return
	}
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, j)
}

func (s *Server) cancelJob(w http.ResponseWriter, r *http.Request) {
	j, err := s.scheduler.Cancel(r.Context(), r.PathValue("id"))
	if errors.Is(err, scheduler.ErrNotImplemented) {
		writeError(w, http.StatusNotImplemented, err.Error())
		return
	}
	if errors.Is(err, store.ErrNotFound) {
		writeError(w, http.StatusNotFound, err.Error())
		return
	}
	if err != nil {
		writeError(w, http.StatusBadRequest, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, j)
}

func writeJSON(w http.ResponseWriter, status int, body any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(body)
}

func writeError(w http.ResponseWriter, status int, message string) {
	writeJSON(w, status, map[string]string{
		"error": strings.TrimSpace(message),
	})
}
