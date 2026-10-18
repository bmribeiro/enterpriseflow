
import { Component, computed, signal } from '@angular/core';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent {

  readonly tasks = signal<Task[]>([
    {
      id: 1,
      title: 'Configurar arquitetura Angular',
      status: 'DONE'
    },
    {
      id: 2,
      title: 'Integrar REST API Spring Boot',
      status: 'TODO'
    },
    {
      id: 3,
      title: 'Implementar autenticação OIDC',
      status: 'TODO'
    }
  ]);

  readonly totalTasks = computed(() => this.tasks().length);

  readonly completedTasks = computed(
    () => this.tasks().filter(task => task.status === 'DONE').length
  );

  readonly pendingTasks = computed(
  () => this.tasks().filter(task => task.status !== 'DONE').length
);

  private nextId = 4;

  addTask(title: string): void {
    const trimmedTitle = title.trim();

    if (!trimmedTitle) {
      return;
    }

    this.tasks.update(tasks => [
      ...tasks,
      {
        id: this.nextId++,
        title: trimmedTitle,
        status: 'TODO'
      }
    ]);
  }

  toggleTask(id: number): void {
    this.tasks.update(tasks =>
      tasks.map(task =>
        task.id === id
          ? {
              ...task,
              status: task.status === 'DONE' ? 'TODO' : 'DONE'
            }
          : task
      )
    );
  }
}
