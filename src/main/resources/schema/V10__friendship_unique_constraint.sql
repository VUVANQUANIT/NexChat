-- V10 — Thêm Unique Constraint cho bảng friendships ở tầng Database
-- Mục tiêu: Ngăn chặn User A gửi nhiều lời mời PENDING đến User B,
--            hoặc tạo nhiều bản ghi ACCEPTED trùng lặp phá vỡ State Machine.
--
-- Tên bảng thực tế: "friendships" (lowercase, theo @Table(name="friendships") trong entity)
-- Tên cột thực tế: requester_id, addressee_id (snake_case, theo @JoinColumn trong entity)
--
-- GHI CHÚ AN TOÀN KHI CHẠY MIGRATION:
--   Bước 1: Xóa các bản ghi trùng lặp (nếu có) trước khi thêm constraint.
--   Bước 2: Thêm Unique Constraint chính thức.
--   Bước 3: Thêm Index tra cứu hai chiều bổ sung.

-- ── Bước 1: Dọn dẹp bản ghi trùng lặp (giữ lại bản ghi có ID nhỏ nhất) ──────
-- CHẠY TRONG TRANSACTION ĐỂ AN TOÀN
DELETE FROM friendships a
USING friendships b
WHERE a.id > b.id
  AND a.requester_id = b.requester_id
  AND a.addressee_id = b.addressee_id;

-- ── Bước 2: Thêm Unique Constraint chính thức ─────────────────────────────────
-- Tên constraint khớp với @UniqueConstraint(name="unique_friendship") trong entity.
-- IF NOT EXISTS đảm bảo idempotent nếu constraint đã được tạo bởi Hibernate DDL trước đó.
ALTER TABLE friendships
    ADD CONSTRAINT IF NOT EXISTS unique_friendship
    UNIQUE (requester_id, addressee_id);

-- ── Bước 3: Index tra cứu ngược (addressee → requester) để tăng tốc query ────
-- Hỗ trợ findBetweenUsers() khi tìm quan hệ theo cặp hai chiều (đã có từ V2 index,
-- nhưng đây là index bổ sung kết hợp status để tối ưu hóa thêm cho các query lọc trạng thái).
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_friendship_addressee_status
    ON friendships (addressee_id, requester_id, status);
