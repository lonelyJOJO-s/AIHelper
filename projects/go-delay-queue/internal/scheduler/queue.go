package scheduler

import (
	"container/heap"
	"time"
)

type queueItem struct {
	jobId string
	runAt time.Time
}

// 任务优先队列
type JobQueue struct {
	items []queueItem
}

func NewJobQueue() *JobQueue {
	q := &JobQueue{}
	heap.Init(q)
	return q
}

func (q *JobQueue) Len() int {
	return len(q.items)
}

func (q *JobQueue) Swap(i, j int) {
	q.items[i], q.items[j] = q.items[j], q.items[i]
}

func (q *JobQueue) Less(i, j int) bool {
	return q.items[i].runAt.Before(q.items[j].runAt)
}

func (q *JobQueue) Push(x any) {
	q.items = append(q.items, x.(queueItem))
}

func (q *JobQueue) Pop() any {
	old := q.items
	n := len(old)
	item := old[n-1]
	q.items = old[:n-1]
	return item
}

func (q *JobQueue) Peek() (string, bool) {
	if q.Len() == 0 {
		return "", false
	}
	return q.items[0].jobId, true
}
