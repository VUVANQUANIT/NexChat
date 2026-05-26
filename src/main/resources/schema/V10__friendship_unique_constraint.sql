-- V10 — Thêm Unique Constraint cho bảng Friendship ở tầng Database
-- Mục tiêu: Ngăn chặn User A gửi nhiều lời mời PENDING đến User B,
--            hoặc tạo nhiều bản ghi ACCEPTED trùng lặp phá vỡ State Machine.
--
-- GHI CHÚ AN TOÀN KHI CHẠY MIGRATION:
--   Bước 1: Xóa các bản ghi trùng lặp (nếu có) trước khi thêm constraint.
--   Bước 2: Thêm Unique Constraint chính thức.
--   Bước 3: Thêm Index tra cứu hai chiều (bidirectional lookup).

-- ── Bước 1: Dọn dẹp bản ghi trùng lặp (giữ lại bản ghi có ID nhỏ nhất) ──────
-- CHẠY TRONG TRANSACTION ĐỂ AN TOÀN
DELETE FROM "Friendship" a
USING "Friendship" b
WHERE a.id > b.id
  AND a."requesterId" = b."requesterId"
  AND a."addresseeId" = b."addresseeId";

-- ── Bước 2: Thêm Unique Constraint ────────────────────────────────────────────
ALTER TABLE "Friendship"
    ADD CONSTRAINT unique_friendship_pair
    UNIQUE ("requesterId", "addresseeId");

-- ── Bước 3: Index tra cứu ngược (addressee → requester) để tăng tốc query ────
-- Index này hỗ trợ findBetweenUsers() khi ta tìm quan hệ theo cặp hai chiều.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_friendship_reverse_lookup
    ON "Friendship" ("addresseeId", "requesterId", status);
