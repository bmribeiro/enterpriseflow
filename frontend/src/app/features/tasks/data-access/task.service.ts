
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  TaskResponse,
  CreateTaskRequest,
  UpdateTaskRequest
} from '../models/task-api.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private readonly http = inject(HttpClient);

  private baseUrl(
    tenantId: string,
    projectId: string
  ): string {
    return `/api/v1/tenants/${encodeURIComponent(tenantId)}` +
           `/projects/${encodeURIComponent(projectId)}/tasks`;
  }

  // READ - LIST
  getTasks(
    tenantId: string,
    projectId: string
  ): Observable<TaskResponse[]> {

    return this.http.get<TaskResponse[]>(
      this.baseUrl(tenantId, projectId)
    );
  }

  // READ - SINGLE
  getTask(
    tenantId: string,
    projectId: string,
    taskId: string
  ): Observable<TaskResponse> {

    return this.http.get<TaskResponse>(
      `${this.baseUrl(tenantId, projectId)}/${encodeURIComponent(taskId)}`
    );
  }

  // CREATE
  createTask(
    tenantId: string,
    projectId: string,
    request: CreateTaskRequest
  ): Observable<TaskResponse> {

    return this.http.post<TaskResponse>(
      this.baseUrl(tenantId, projectId),
      request
    );
  }

  // UPDATE
  updateTask(
    tenantId: string,
    projectId: string,
    taskId: string,
    request: UpdateTaskRequest
  ): Observable<TaskResponse> {

    return this.http.put<TaskResponse>(
      `${this.baseUrl(tenantId, projectId)}/${encodeURIComponent(taskId)}`,
      request
    );
  }

  // DELETE
  deleteTask(
    tenantId: string,
    projectId: string,
    taskId: string
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.baseUrl(tenantId, projectId)}/${encodeURIComponent(taskId)}`
    );
  }
}
