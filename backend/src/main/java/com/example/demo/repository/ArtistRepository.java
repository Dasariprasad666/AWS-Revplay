package com.example.demo.repository;

import com.example.demo.entity.Artist;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // Finds an artist profile based on the logged-in user account
    Optional<Artist> findByUser(User user);

    Optional<Artist> findByUser_Email(String email);
}