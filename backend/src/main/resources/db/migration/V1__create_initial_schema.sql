-- ==========================================
-- EnterpriseFlow - Initial Database Schema
-- ==========================================

-- Tenants
CREATE TABLE tenants
(
    id   UUID PRIMARY KEY,
    code VARCHAR(50)  NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL
);

-- Projects
CREATE TABLE projects
(
    id          UUID PRIMARY KEY,
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(1000),

    CONSTRAINT fk_projects_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenants (id)
);

-- Tasks
CREATE TABLE tasks
(
    id         UUID PRIMARY KEY,
    project_id UUID         NOT NULL,
    title      VARCHAR(200) NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'TODO',

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id)
            REFERENCES projects (id),

    CONSTRAINT chk_tasks_status
        CHECK (status IN (
                          'TODO',
                          'IN_PROGRESS',
                          'DONE'
            ))
);

-- Indexes
CREATE INDEX idx_projects_tenant_id
    ON projects (tenant_id);

CREATE INDEX idx_tasks_project_id
    ON tasks (project_id);
