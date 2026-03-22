package com.Spring_chat.Spring_chat.repository;

import com.Spring_chat.Spring_chat.ENUM.FriendshipStatus;
import com.Spring_chat.Spring_chat.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByRequester_IdAndAddressee_IdAndStatus(Long requesterId, Long addresseeId, FriendshipStatus status);
}
