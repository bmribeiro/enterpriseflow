CREATE TABLE tenant_memberships
(

    issuer    VARCHAR(255) NOT NULL,

    subject   VARCHAR(255) NOT NULL,

    tenant_id UUID         NOT NULL,

    role      VARCHAR(20)  NOT NULL DEFAULT 'USER',

    CONSTRAINT pk_tenant_memberships
        PRIMARY KEY (issuer, subject, tenant_id),

    CONSTRAINT fk_membership_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenants (id),

    CONSTRAINT chk_membership_role
        CHECK (role IN ('USER', 'ADMIN'))
);

CREATE INDEX idx_tenant_memberships_tenant_id
    ON tenant_memberships (tenant_id);
