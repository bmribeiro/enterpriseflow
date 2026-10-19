
import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal
} from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { TaskService } from '../../data-access/task.service';
import { TaskResponse } from '../../models/task-api.model';

import { DEV_CONTEXT } from '../../../../core/config/dev-context';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {

  private readonly taskService = inject(TaskService);
  private readonly destroyRef = inject(DestroyRef);

  readonly tasks = signal<TaskResponse[]>([]);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly totalTasks = computed(
    () => this.tasks().length
  );

  readonly completedTasks = computed(
    () => this.tasks().filter(
      task => task.status === 'DONE'
    ).length
  );

  readonly pendingTasks = computed(
    () => this.tasks().filter(
      task => task.status !== 'DONE'
    ).length
  );

  readonly creating = signal(false);

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {

    this.loading.set(true);
    this.error.set(null);

    this.taskService.getTasks(
      DEV_CONTEXT.tenantId,
      DEV_CONTEXT.projectId
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (tasks) => {
          this.tasks.set(tasks);
          this.loading.set(false);
        },

        error: (error: unknown) => {
          console.error(
            'Erro ao carregar tarefas:',
            error
          );

          this.error.set(
            this.getErrorMessage(error)
          );

          this.loading.set(false);
        }
      });
  }

  private getErrorMessage(error: unknown): string {

    if (error instanceof HttpErrorResponse) {
      switch (error.status) {
        case 0:
          return 'Não foi possível comunicar com a API.';

        case 401:
          return 'A API não autorizou a sessão.';

        case 403:
          return 'Não tem permissão para consultar estas tarefas.';

        case 404:
          return 'O recurso solicitado não foi encontrado.';

        default:
          return `Erro HTTP ${error.status}.`;
      }
    }

    return 'Ocorreu um erro inesperado.';
  }


  createTask(input: HTMLInputElement): void {

    const title = input.value.trim();

    if (!title || title.length > 200 || this.creating()) {
      return;
    }

    this.creating.set(true);
    this.error.set(null);

    this.taskService.createTask(
      DEV_CONTEXT.tenantId,
      DEV_CONTEXT.projectId,
      { title }
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (createdTask) => {

          console.log(
            'Tarefa criada:',
            createdTask.id
          );

          input.value = '';
          this.creating.set(false);

          // Recarrega os dados a partir do backend.
          this.loadTasks();
        },

        error: (error: unknown) => {

          console.error(
            'Erro ao criar tarefa:',
            error
          );

          this.error.set(
            this.getErrorMessage(error)
          );

          this.creating.set(false);
        }
      });
  }

}
