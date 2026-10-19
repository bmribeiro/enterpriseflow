
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { TaskListComponent } from './task-list.component';
import { TaskService } from '../../data-access/task.service';

import { DEV_CONTEXT } from '../../../../core/config/dev-context';

describe('TaskListComponent', () => {

  const taskServiceMock = {
    getTasks: vi.fn()
  };

  beforeEach(async () => {

    taskServiceMock.getTasks.mockReset();
    taskServiceMock.getTasks.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        {
          provide: TaskService,
          useValue: taskServiceMock
        }
      ]
    }).compileComponents();
  });

  it('should create the component', () => {

    const fixture =
      TestBed.createComponent(TaskListComponent);

    fixture.detectChanges();

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should load tasks from the service', () => {

    const fixture =
      TestBed.createComponent(TaskListComponent);

    fixture.detectChanges();

    expect(taskServiceMock.getTasks).toHaveBeenCalledWith(
      DEV_CONTEXT.tenantId,
      DEV_CONTEXT.projectId
    );

    expect(fixture.componentInstance.totalTasks()).toBe(0);
  });

});
