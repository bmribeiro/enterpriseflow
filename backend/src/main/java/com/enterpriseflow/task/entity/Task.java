
package com.enterpriseflow.task.entity;

import com.enterpriseflow.project.entity.Project;
import com.enterpriseflow.task.enums.TaskStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TaskStatus status;

    protected Task() {
    }

    public Task(Project project, String title) {
        this.project = project;
        this.title = title;
        this.status = TaskStatus.TODO;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
