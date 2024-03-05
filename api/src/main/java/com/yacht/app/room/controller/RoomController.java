package com.yacht.app.room.controller;

import com.yacht.app.room.dto.RoomDto;
import com.yacht.app.room.service.RoomService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/room")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto.DetailResponse> create(@NonNull @RequestBody RoomDto.Create create) {
        return ResponseEntity.ok(roomService.create(create));
    }

    @GetMapping
    public ResponseEntity<List<RoomDto.Response>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto.DetailResponse> get(@NonNull @PathVariable String id, @NonNull RoomDto.DetailRequest detailRequest) {
        return ResponseEntity.ok(roomService.get(id, detailRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto.DetailResponse> update(@NonNull @PathVariable String id, @NonNull @RequestBody RoomDto.Update update) {
        return ResponseEntity.ok(roomService.update(id, update));
    }

    @DeleteMapping("/{id}")
    public void delete(@NonNull @PathVariable String id) {
        roomService.delete(id);
    }

    @DeleteMapping
    @Secured("ROLE_USER")
    public void deleteAll(@NonNull RoomDto.DeleteAll deleteAll) {
        roomService.deleteAll(deleteAll);
    }

    @MessageMapping("/{id}/online")
    public void online(@NonNull @DestinationVariable String id, RoomDto.Online online) {
        roomService.online(id, online);
    }

    @MessageMapping("/{id}/chat")
    public void chat(@NonNull @DestinationVariable String id, RoomDto.Chat chat) {
        roomService.chat(id, chat);
    }

    @MessageMapping("/{id}/add-team")
    public void addTeam(@NonNull @DestinationVariable String id) {
        roomService.addTeam(id);
    }

    @MessageMapping("/{id}/join-team")
    public void joinTeam(@NonNull @DestinationVariable String id, RoomDto.JoinTeamRequest joinTeamRequest) {
        roomService.joinTeam(id, joinTeamRequest);
    }

    @MessageMapping("/{id}/remove-team")
    public void removeTeam(@NonNull @DestinationVariable String id, RoomDto.RemoveTeamRequest removeTeamRequest) {
        roomService.removeTeam(id, removeTeamRequest);
    }

    @MessageMapping("/{id}/play")
    public void play(@NonNull @DestinationVariable String id, RoomDto.PlayRequest playRequest) {
        roomService.play(id, playRequest);
    }

    @MessageMapping("/{id}/dice-roll")
    public void diceRoll(@NonNull @DestinationVariable String id, RoomDto.DiceRollRequest diceRollRequest) {
        roomService.diceRoll(id, diceRollRequest);
    }

    @MessageMapping("/{id}/set-dice-value")
    public void setDiceValue(@NonNull @DestinationVariable String id, RoomDto.SetDiceValueRequest setDiceValueRequest) {
        roomService.setDiceValue(id, setDiceValueRequest);
    }

    @MessageMapping("/{id}/set-score")
    public void setScore(@NonNull @DestinationVariable String id, RoomDto.SetScoreRequest setScoreRequest) {
        roomService.setScore(id, setScoreRequest);
    }

    @MessageMapping("/{id}/keep-dice")
    public void keepDice(@NonNull @DestinationVariable String id, RoomDto.KeepDiceRequest keepDiceRequest) {
        roomService.keepDice(id, keepDiceRequest);
    }

    @MessageMapping("/{id}/un-keep-dice")
    public void unKeepDice(@NonNull @DestinationVariable String id, RoomDto.UnKeepDiceRequest unKeepDiceRequest) {
        roomService.unKeepDice(id, unKeepDiceRequest);
    }

}
