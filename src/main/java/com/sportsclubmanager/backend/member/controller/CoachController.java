package com.sportsclubmanager.backend.member.controller;

import java.util.List;
import java.util.Optional;

import com.sportsclubmanager.backend.user.model.AffiliationStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sportsclubmanager.backend.member.model.Coach;
import com.sportsclubmanager.backend.user.dto.UserResponse;
import com.sportsclubmanager.backend.user.dto.UserUpdateRequest;
import com.sportsclubmanager.backend.user.mapper.UserMapper;
import com.sportsclubmanager.backend.user.service.UserService;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    private final UserService<Coach> coachService;

    private final UserMapper userMapper;

    public CoachController(@Qualifier("coachService") UserService<Coach> coachService, UserMapper userMapper) {
        this.coachService = coachService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<Coach> create(@RequestBody Coach coach) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachService.save(coach));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return coachService.findById(id)
                .map(coach -> ResponseEntity.ok(userMapper.toUserResponse(coach)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return coachService.findByUsername(username)
                .map(coach -> ResponseEntity.ok(userMapper.toUserResponse(coach)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(coachService.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList());
    }

    @GetMapping("/page/{page}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllPaginated(@PathVariable Integer page) {
        return ResponseEntity.ok(coachService.findAll(PageRequest.of(page, 5))
                .map(userMapper::toUserResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'COACH', 'ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
            @RequestBody UserUpdateRequest userUpdateRequest) {

        return coachService.update(id, userUpdateRequest)
                .map(user -> ResponseEntity.ok(userMapper.toUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Coach> coachOptional = coachService.findById(id);

        if (coachOptional.isPresent()) {
            coachService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/change-affiliation-status/{id}")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> updateAffiliationStatus(@PathVariable Long id, @RequestBody AffiliationStatus affiliationStatus) {
        boolean updated = coachService.updateAffiliationStatus(id, affiliationStatus);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
