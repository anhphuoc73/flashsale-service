package com.flashsale.session.service;

import com.flashsale.flashsale.repository.FlashSaleItemRepository;
import com.flashsale.session.dto.request.CreateFlashSaleSessionRequest;
import com.flashsale.session.dto.request.UpdateFlashSaleSessionRequest;
import com.flashsale.session.dto.response.FlashSaleSessionResponse;
import com.flashsale.session.entity.FlashSaleSession;
import com.flashsale.session.enums.SessionStatus;
import com.flashsale.session.repository.FlashSaleSessionRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FlashSaleSessionService {

    private final FlashSaleSessionRepository repository;
    private final FlashSaleItemRepository flashSaleItemRepository;

    public FlashSaleSessionResponse create(CreateFlashSaleSessionRequest request){

        if(request.getEndTime().isBefore(request.getStartTime())){
            throw new RuntimeException("End time must be after start time");
        }

        if(request.getStartTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Start time must be in the future");
        }

        List<FlashSaleSession> overlaps =
                repository.findByActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Flash sale session time overlaps with existing session");
        }

        FlashSaleSession session = new FlashSaleSession();
        session.setName(request.getName());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setActive(request.getActive() != null ? request.getActive() : true);

        repository.save(session);

        return FlashSaleSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .active(session.isActive())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();

    }

    public FlashSaleSessionResponse update(String id, UpdateFlashSaleSessionRequest request){

        FlashSaleSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlashSaleSession not found"));

        LocalDateTime now = LocalDateTime.now();

        // 1. Không cho update nếu đã bắt đầu
        if (!session.getStartTime().isAfter(now)) {
            throw new RuntimeException("Cannot update session that already started");
        }


        // 2. update startTime
        if (request.getStartTime() != null) {
            if (request.getStartTime().isBefore(now)) {
                throw new RuntimeException("Start time must be in the future");
            }
            session.setStartTime(request.getStartTime());
        }

        // 3. update endTime
        if (request.getEndTime() != null) {
            session.setEndTime(request.getEndTime());
        }


        if(session.getEndTime().isBefore(session.getStartTime())){
            throw new RuntimeException("End time must be after start time");
        }

        // 4. Check overlap
        List<FlashSaleSession> overlaps =
                repository.findByActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                                session.getEndTime(),
                                session.getStartTime()
                        ).stream()
                        .filter(s -> !s.getId().equals(id))
                        .toList();

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Flash sale session time overlaps with existing session");
        }

        // 5. update name
        if(request.getName() != null){
            session.setName(request.getName());
        }

        if (request.getActive() != null) {
            session.setActive(request.getActive());
        }

        repository.save(session);

        return FlashSaleSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .active(session.isActive())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public void delete(String id){

        FlashSaleSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlashSaleSession not found"));

        LocalDateTime now = LocalDateTime.now();

        // 1. Không cho delete nếu đã bắt đầu
        if (!session.getStartTime().isAfter(now)) {
            throw new RuntimeException("Cannot delete session that already started");
        }

        // 2. Không cho delete nếu đã có item
        if (flashSaleItemRepository.existsBySessionId(id)) {
            throw new RuntimeException("Cannot delete session that already has flash sale items");
        }

        if (!session.isActive()) {
            throw new RuntimeException("Session already deleted");
        }

        session.setActive(false);
        repository.save(session);
    }

    public Page<FlashSaleSessionResponse> getAdminSessions(Pageable pageable) {

        Page<FlashSaleSession> page = repository.findAll(pageable);

        LocalDateTime now = LocalDateTime.now();

        return page.map(session -> FlashSaleSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .active(session.isActive())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .status(resolveStatus(session, now))
                .build());
    }

    private FlashSaleSessionResponse mapToResponse(
            FlashSaleSession session,
            LocalDateTime now
    ){
        return FlashSaleSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(resolveStatus(session, now))
                .build();
    }
    private SessionStatus resolveStatus(
            FlashSaleSession session,
            LocalDateTime now
    ){
        if(now.isBefore(session.getStartTime())){
            return SessionStatus.UPCOMING;
        }
        if (now.isAfter(session.getEndTime())) {
            return SessionStatus.ENDED;
        }
        return SessionStatus.ONGOING;
    }




}
