
export type TaskStatus =
  | 'TODO'
  | 'IN_PROGRESS'
  | 'DONE';

export interface TaskResponse {
  id: string;
  projectId: string;
  title: string;
  status: TaskStatus;
}

export interface CreateTaskRequest {
  title: string;
}

export interface UpdateTaskRequest {
  title: string;
  status: TaskStatus;
}
