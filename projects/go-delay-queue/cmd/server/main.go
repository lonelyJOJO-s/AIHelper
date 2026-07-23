package main

import (
	"context"
	"errors"
	"log"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/api"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/scheduler"
	"github.com/lonelyJOJO-s/AIHelper/projects/go-delay-queue/internal/store"
)

func main() {
	st := store.NewMemoryStore()
	sched := scheduler.New(st)
	server := api.NewServer(st, sched)

	httpServer := &http.Server{
		Addr:              ":8081",
		Handler:           server.Handler(),
		ReadHeaderTimeout: 5 * time.Second,
	}

	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
		Level: slog.LevelInfo,
	}))
	slog.SetDefault(logger)

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	go func() {
		if err := sched.Start(ctx); err != nil && !errors.Is(err, context.Canceled) {
			log.Printf("scheduler stopped: %v", err)
		}
	}()

	go func() {
		<-ctx.Done()
		shutdownCtx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
		_ = httpServer.Shutdown(shutdownCtx)
	}()

	log.Printf("delay queue listening on %s", httpServer.Addr)
	if err := httpServer.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		log.Fatalf("server failed: %v", err)
	}
}
