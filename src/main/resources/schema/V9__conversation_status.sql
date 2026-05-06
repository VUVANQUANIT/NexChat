-- V9 — Conversation.status (matches Conversation entity / ConversationStatus enum)
-- Apply manually on PostgreSQL when using ddl-auto=validate, or after restoring an old DB snapshot.

ALTER TABLE conversations
    ADD COLUMN IF NOT EXISTS status varchar(20) NOT NULL DEFAULT 'ACTIVE';
