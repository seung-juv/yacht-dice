package com.yacht.app.room.service;

import com.yacht.app.room.domain.*;
import com.yacht.app.room.dto.*;
import com.yacht.app.user.domain.Account;
import com.yacht.app.user.domain.AccountRepository;
import com.yacht.app.user.dto.UserDto;
import com.yacht.app.user.dto.UserDtoMapper;
import com.yacht.exception.BadRequestException;
import com.yacht.exception.ForbiddenException;
import com.yacht.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final AccountRepository accountRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private List<Room> rooms = new ArrayList<>();

    public RoomDto.DetailResponse create(@NonNull RoomDto.Create create) {
        Room room = RoomDtoMapper.INSTANCE.toEntity(create);
        room.setStatus(Room.Status.READY);
        RoomUser roomUser = new RoomUser();
        roomUser.setIsMaster(true);
        roomUser.setUuid(create.getUser().getUuid());
        roomUser.setName(create.getUser().getName());
        room.getRoomUsers().add(roomUser);
        // Set Initial Dices
        for (int i = 0; i < room.getDiceLength(); i++) {
            RoomDice roomDice = new RoomDice();
            roomDice.setValue(6);
            RoomDice.Position position = new RoomDice.Position();
            position.setX(
                    (-room.getDiceLength() / 2) * (2 + 0.5)
                            + (i + 0.5) * (2 + 0.5)
            );
            position.setY(1.0);
            position.setZ(0.0);
            roomDice.setPosition(position);
            room.getRoomDices().add(roomDice);
        }
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            room.getRoomUsers().removeIf(roomRoomUser -> LocalDateTime.now().isAfter(roomRoomUser.getOnlineAt().plusSeconds(3)));
            if (room.getRoomUsers().isEmpty()) {
                executor.shutdown();
                this.delete(room.getId());
                return;
            }
            if (room.getRoomUsers().stream().noneMatch(RoomUser::getIsMaster)) {
                simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/no-master");
                executor.shutdown();
                this.delete(room.getId());
            }
        }, 0, 1, TimeUnit.SECONDS);
        this.rooms.add(room);
        return RoomDtoMapper.INSTANCE.toDetailResponse(room);
    }

    public RoomDto.DetailResponse update(@NonNull String id, @NonNull RoomDto.Update update) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomDtoMapper.INSTANCE.merge(update, room);
        return RoomDtoMapper.INSTANCE.toDetailResponse(room);
    }

    public List<RoomDto.Response> getAll() {
        return this.rooms.stream().map(RoomDtoMapper.INSTANCE::toResponse).toList();
    }

    private RoomDto.DetailResponse toDetailResponse(@NonNull Room room) {
        return RoomDtoMapper.INSTANCE.toDetailResponse(room);
    }

    public RoomDto.DetailResponse get(@NonNull String id, @NonNull RoomDto.DetailRequest detailRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        if (room.getIsSecret()) {
            if (!room.getSecretPassword().equals(detailRequest.getPassword())) {
                throw new ForbiddenException("Password does not match");
            }
        }
        return this.toDetailResponse(room);
    }

    public void delete(@NonNull String id) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        this.rooms.remove(room);
    }

    public void deleteAll(@NonNull RoomDto.DeleteAll deleteAll) {
        this.rooms = this.rooms.stream().filter((x) -> deleteAll.getIds().stream().noneMatch(y -> y.equals(x.getId()))).toList();
    }

    public void messageSendUsers(@NonNull Room room) {
        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/users", room.getRoomUsers().stream().map(RoomUserDtoMapper.INSTANCE::toResponse).toList());
    }

    public void online(@NonNull String id, @NonNull RoomDto.Online online) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        Optional<RoomUser> roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(online.getUuid())).findFirst();
        if (roomUser.isPresent()) {
            RoomUser roomUser1 = roomUser.get();
            roomUser1.setOnlineAt(LocalDateTime.now());
        } else {
            RoomUser roomUser1 = new RoomUser();
            roomUser1.setUuid(online.getUuid());
            roomUser1.setName(online.getName());
            room.getRoomUsers().add(roomUser1);
        }
        messageSendUsers(room);
    }

    public void chat(@NonNull String id, @NonNull RoomDto.Chat chat) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));

        RoomChat roomChat = new RoomChat();

        if (chat.getAccountId() != null) {
            Account account = accountRepository.findById(chat.getAccountId()).orElseThrow(() -> new NotFoundException("Not found Account"));
            roomChat.setCreatedBy(account.getUser());

            UserDto.Response user = UserDtoMapper.INSTANCE.toResponse(account);
            user.setName(account.getUser().getName());
        }

        roomChat.setRoom(room);
        roomChat.setName(chat.getName());
        roomChat.setContent(chat.getMessage());
        room.getRoomChats().add(roomChat);

        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + id + "/chat", RoomChatDtoMapper.INSTANCE.toResponse(roomChat));
    }

    public void messageSendTeams(@NonNull Room room) {
        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/teams", room.getRoomTeams().stream().map(RoomTeamDtoMapper.INSTANCE::toResponse).toList());
    }

    public void addTeam(@NonNull String id) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));

        if (room.getStatus().equals(Room.Status.PLAYING)) {
            throw new ForbiddenException("Cannot be added while in playing");
        }

        RoomTeam roomTeam = new RoomTeam();
        roomTeam.setName("Team");
        room.getRoomTeams().add(roomTeam);

        messageSendTeams(room);
    }

    public void joinTeam(@NonNull String id, @NonNull RoomDto.JoinTeamRequest roomJoinRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));

        if (room.getStatus().equals(Room.Status.PLAYING)) {
            throw new ForbiddenException("Cannot be joined while in playing");
        }

        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(roomJoinRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found User"));
        RoomTeam roomTeam = room.getRoomTeams().stream().filter(x -> x.getId().equals(roomJoinRequest.getTeamId())).findFirst().orElseThrow(() -> new NotFoundException("Not found Team"));

        // 이미 팀에 들어가있는 경우
        if (roomUser.getRoomTeam() != null) {
            // 팀이 같을 경우
            if (roomUser.getRoomTeam().equals(roomTeam)) {
                throw new ForbiddenException("Already join team");
            }
            // 팀이 다를 경우
            else {
                // 기존 팀 나가기
                roomUser.getRoomTeam().getRoomUsers().remove(roomUser);
                // 팀 덮어쓰기
                roomUser.setRoomTeam(roomTeam);
                // 팀에 추가
                roomTeam.getRoomUsers().add(roomUser);
            }
        } else {
            // 팀 설정
            roomUser.setRoomTeam(roomTeam);
            // 팀에 추가
            roomTeam.getRoomUsers().add(roomUser);
        }

        messageSendUsers(room);
        messageSendTeams(room);
    }

    public void removeTeam(@NonNull String id, @NonNull RoomDto.RemoveTeamRequest removeTeamRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));

        if (room.getStatus().equals(Room.Status.PLAYING)) {
            throw new ForbiddenException("Cannot be deleted while in playing");
        }

        RoomTeam roomTeam = room.getRoomTeams().stream().filter(x -> x.getId().equals(removeTeamRequest.getTeamId())).findFirst().orElseThrow(() -> new NotFoundException("Not found Team"));

        // 팀 안에 있던 유저 제거
        for (RoomUser roomUser : roomTeam.getRoomUsers()) {
            roomUser.setRoomTeam(null);
        }

        // 팀 제거
        room.getRoomTeams().remove(roomTeam);

        messageSendUsers(room);
        messageSendTeams(room);
    }

    private void sendMessageDices(@NonNull Room room) {
        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/dices", room.getRoomDices().stream().map(RoomDiceDtoMapper.INSTANCE::toResponse).toList());
    }

    private void nextTurn(@NonNull Room room) {
        room.setCurrentDiceRollCount(0);

        int nextRoomTeamIndex = 0;

        // 진행중인 팀이 있을 경우
        if (room.getProgressRoomTeam() != null) {
            int progressRoomTeamIndex = room.getRoomTeams().indexOf(room.getProgressRoomTeam());
            nextRoomTeamIndex = progressRoomTeamIndex + 1;
            if (nextRoomTeamIndex > room.getRoomTeams().size() - 1) {
                nextRoomTeamIndex = 0;
            }
        }

        RoomTeam nextRoomTeam = room.getRoomTeams().get(nextRoomTeamIndex);
        room.setProgressRoomTeam(nextRoomTeam);

        // 주사위 초기 값으로 변경
        for (RoomDice roomDice : room.getRoomDices()) {
            roomDice.setValue(6);
            RoomDice.Position position = new RoomDice.Position();
            position.setX(
                    (-room.getDiceLength() / 2) * (2 + 0.5)
                            + (room.getRoomDices().indexOf(roomDice) + 0.5) * (2 + 0.5)
            );
            position.setY(1.0);
            position.setZ(0.0);
            roomDice.setPosition(position);
            roomDice.setRotation(new RoomDice.Rotation());
            roomDice.setStatus(0);
        }

        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/turn", RoomDtoMapper.INSTANCE.toTurnResponse(room));
        this.sendMessageDices(room);
    }

    public void diceRoll(@NonNull String id, @NonNull RoomDto.DiceRollRequest diceRollRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        if (room.getStatus().equals(Room.Status.PLAYING)) {
            RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(diceRollRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));
            room.getRoomTeams().stream().filter(x -> x.getRoomUsers().contains(roomUser)).findFirst().orElseThrow(() -> new ForbiddenException("Can only do your team turn"));
            if (room.getCurrentDiceRollCount() > 2) {
                throw new ForbiddenException("Can no longer roll the dice");
            }
            room.setCurrentDiceRollCount(room.getCurrentDiceRollCount() + 1);
        }

        for (RoomDiceDto.Request dice : diceRollRequest.getDices()) {
            RoomDice roomDice = room.getRoomDices().stream().filter(x -> x.getId().equals(dice.getId())).findFirst().orElseThrow(() -> new NotFoundException("Not found Dice"));
            RoomDiceDtoMapper.INSTANCE.merge(dice, roomDice);
            switch (roomDice.getStatus()) {
                case 1 -> {
                    roomDice.setIsKeep(true);
                }
                case 2 -> {
                    roomDice.setIsKeep(false);
                }
            }
            roomDice.setStatus(0);
        }

        this.sendMessageDices(room);
    }

    public void setDiceValue(@NonNull String id, @NonNull RoomDto.SetDiceValueRequest setDiceValueRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(setDiceValueRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));
        room.getRoomTeams().stream().filter(x -> x.getRoomUsers().contains(roomUser)).findFirst().orElseThrow(() -> new ForbiddenException("Can only do your team turn"));
        room.setRoomDices(setDiceValueRequest.getDices().stream().map(RoomDiceDtoMapper.INSTANCE::toEntity).toList());
        RoomDto.SetDiceValueResponse diceValueResponse = RoomDtoMapper.INSTANCE.toSetDiceValueResponse(room);
        diceValueResponse.setUuid(setDiceValueRequest.getUuid());
        try {
            RoomTeamScore score = this.toScore(room.getRoomDices(), room.getProgressRoomTeam().getScore());
            room.setRoomTeamTempScore(score);
            diceValueResponse.setScore(RoomTeamScoreDtoMapper.INSTANCE.toResponse(score));
            simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/dice-value", diceValueResponse);
            this.sendMessageDices(room);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void play(@NonNull String id, @NonNull RoomDto.PlayRequest playRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(playRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));

        if (!roomUser.getIsMaster()) {
            throw new ForbiddenException("Only master can do it");
        }

        if (!room.getStatus().equals(Room.Status.READY)) {
            throw new ForbiddenException("It can only be executed during ready");
        }

        if (
            // 팀이 없을 경우
                room.getRoomTeams().isEmpty()
                        // 모든 팀에 유저가 없을 경우
                        || room.getRoomTeams().stream().allMatch(x -> x.getRoomUsers().isEmpty())
        ) {
            throw new ForbiddenException("Only need to have a team to get started");
        }

        // 비어있는 팀 제거
        room.getRoomTeams().removeIf(x -> x.getRoomUsers().isEmpty());

        room.setStatus(Room.Status.PLAYING);
        simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + id + "/status", RoomDtoMapper.INSTANCE.toStatusResponse(room));

        nextTurn(room);
    }

    public RoomTeamScore toScore(@NonNull List<RoomDice> dices, @NonNull RoomTeamScore roomTeamScore) throws CloneNotSupportedException {
        List<Integer> diceValues = dices.stream().map(RoomDice::getValue).toList();

        RoomTeamScore roomTeamScore1 = roomTeamScore.clone();
        HashMap<Integer, Integer> diceCountMap = new HashMap<>();

        for (Integer diceValue : diceValues) {
            diceCountMap.put(diceValue, diceCountMap.getOrDefault(diceValue, 0) + 1);
        }

        Integer sumDices = diceValues.stream().reduce(Integer::sum).orElseThrow(() -> new NotFoundException("Not found Dices"));

        // Upper Section
        if (roomTeamScore1.getUpperSection().getAces() == null) {
            roomTeamScore1.getUpperSection().setAces(diceCountMap.getOrDefault(1, 0));
        }
        if (roomTeamScore1.getUpperSection().getTwos() == null) {
            roomTeamScore1.getUpperSection().setTwos(diceCountMap.getOrDefault(2, 0) * 2);
        }
        if (roomTeamScore1.getUpperSection().getThrees() == null) {
            roomTeamScore1.getUpperSection().setThrees(diceCountMap.getOrDefault(3, 0) * 3);
        }
        if (roomTeamScore1.getUpperSection().getFours() == null) {
            roomTeamScore1.getUpperSection().setFours(diceCountMap.getOrDefault(4, 0) * 4);
        }
        if (roomTeamScore1.getUpperSection().getFives() == null) {
            roomTeamScore1.getUpperSection().setFives(diceCountMap.getOrDefault(5, 0) * 5);
        }
        if (roomTeamScore1.getUpperSection().getSixes() == null) {
            roomTeamScore1.getUpperSection().setSixes(diceCountMap.getOrDefault(6, 0) * 6);
        }

        // Lower Section
        for (Integer count : diceCountMap.values()) {
            // Three Of A Kind
            if (roomTeamScore1.getLowerSection().getThreeOfAKind() == null) {
                if (count >= 3) {
                    roomTeamScore1.getLowerSection().setThreeOfAKind(sumDices);
                } else {
                    roomTeamScore1.getLowerSection().setThreeOfAKind(0);
                }
            }
            // Four Of A Kind
            if (roomTeamScore1.getLowerSection().getFourOfAKind() == null) {
                if (count >= 4) {
                    roomTeamScore1.getLowerSection().setFourOfAKind(sumDices);
                } else {
                    roomTeamScore1.getLowerSection().setFourOfAKind(0);
                }
            }
        }
        // Full House
        if (roomTeamScore1.getLowerSection().getFullHouse() == null) {
            if (diceCountMap.containsValue(3) && diceCountMap.containsValue(2)) {
                roomTeamScore1.getLowerSection().setFullHouse(25);
            } else {
                roomTeamScore1.getLowerSection().setFullHouse(0);
            }
        }

        // Small Straight
        if (roomTeamScore1.getLowerSection().getSmallStraight() == null) {
            if (
                    (diceValues.contains(1) && diceValues.contains(2) && diceValues.contains(3) && diceValues.contains(4))
                            || (diceValues.contains(2) && diceValues.contains(3) && diceValues.contains(4) && diceValues.contains(5))
                            || (diceValues.contains(3) && diceValues.contains(4) && diceValues.contains(5) && diceValues.contains(6))
            ) {
                roomTeamScore1.getLowerSection().setSmallStraight(30);
            } else {
                roomTeamScore1.getLowerSection().setSmallStraight(0);
            }
        }
        // Large Straight
        if (roomTeamScore1.getLowerSection().getLargeStraight() == null) {
            if (
                    (diceValues.contains(1) && diceValues.contains(2) && diceValues.contains(3) && diceValues.contains(4) && diceValues.contains(5))
                            || (diceValues.contains(2) && diceValues.contains(3) && diceValues.contains(4) && diceValues.contains(5) && diceValues.contains(6))
            ) {
                roomTeamScore1.getLowerSection().setLargeStraight(40);
            } else {
                roomTeamScore1.getLowerSection().setLargeStraight(0);
            }
        }

        // Chance
        if (roomTeamScore1.getLowerSection().getChance() == null) {
            roomTeamScore1.getLowerSection().setChance(sumDices);
        }

        // Yahtzee
        if (roomTeamScore1.getLowerSection().getYahtzee() == null) {
            if (diceCountMap.containsValue(5)) {
                roomTeamScore1.getLowerSection().setYahtzee(50);
            } else {
                roomTeamScore1.getLowerSection().setYahtzee(0);
            }
        }

        return roomTeamScore1;
    }

    public void setScore(@NonNull String id, @NonNull RoomDto.SetScoreRequest setScoreRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(setScoreRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));
        RoomTeam roomTeam = room.getRoomTeams().stream().filter(x -> x.getRoomUsers().contains(roomUser)).findFirst().orElseThrow(() -> new ForbiddenException("Can only do your team turn"));
        try {
            RoomTeamScore roomTeamScore = this.toScore(room.getRoomDices(), roomTeam.getScore());
            switch (setScoreRequest.getScoreKey()) {
                case "aces":
                    if (roomTeam.getScore().getUpperSection().getAces() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setAces(roomTeamScore.getUpperSection().getAces());
                    break;
                case "twos":
                    if (roomTeam.getScore().getUpperSection().getTwos() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setTwos(roomTeamScore.getUpperSection().getTwos());
                    break;
                case "threes":
                    if (roomTeam.getScore().getUpperSection().getThrees() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setThrees(roomTeamScore.getUpperSection().getThrees());
                    break;
                case "fours":
                    if (roomTeam.getScore().getUpperSection().getFours() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setFours(roomTeamScore.getUpperSection().getFours());
                    break;
                case "fives":
                    if (roomTeam.getScore().getUpperSection().getFives() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setFives(roomTeamScore.getUpperSection().getFives());
                    break;
                case "sixes":
                    if (roomTeam.getScore().getUpperSection().getSixes() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getUpperSection().setSixes(roomTeamScore.getUpperSection().getSixes());
                    break;
                case "threeOfAKind":
                    if (roomTeam.getScore().getLowerSection().getThreeOfAKind() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setThreeOfAKind(roomTeamScore.getLowerSection().getThreeOfAKind());
                    break;
                case "fourOfAKind":
                    if (roomTeam.getScore().getLowerSection().getFourOfAKind() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setFourOfAKind(roomTeamScore.getLowerSection().getFourOfAKind());
                    break;
                case "fullHouse":
                    if (roomTeam.getScore().getLowerSection().getFullHouse() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setFullHouse(roomTeamScore.getLowerSection().getFullHouse());
                    break;
                case "smallStraight":
                    if (roomTeam.getScore().getLowerSection().getSmallStraight() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setSmallStraight(roomTeamScore.getLowerSection().getSmallStraight());
                    break;
                case "largeStraight":
                    if (roomTeam.getScore().getLowerSection().getLargeStraight() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setLargeStraight(roomTeamScore.getLowerSection().getLargeStraight());
                    break;
                case "chance":
                    if (roomTeam.getScore().getLowerSection().getChance() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setChance(roomTeamScore.getLowerSection().getChance());
                    break;
                case "yahtzee":
                    if (roomTeam.getScore().getLowerSection().getYahtzee() != null) {
                        throw new BadRequestException("Already Set Scored");
                    }
                    roomTeam.getScore().getLowerSection().setYahtzee(roomTeamScore.getLowerSection().getYahtzee());
                    break;
                default:
                    throw new BadRequestException("Not allowed Score Key");
            }
            simpMessageSendingOperations.convertAndSend("/ws/room/subscribe/" + room.getId() + "/set-score", RoomDtoMapper.INSTANCE.toSetScoreResponse(room));
            this.nextTurn(room);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void keepDice(@NonNull @DestinationVariable String id, RoomDto.KeepDiceRequest keepDiceRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(keepDiceRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));
        room.getRoomTeams().stream().filter(x -> x.getRoomUsers().contains(roomUser)).findFirst().orElseThrow(() -> new ForbiddenException("Can only do your team turn"));
        RoomDice roomDice = room.getRoomDices().stream().filter(x -> x.getId().equals(keepDiceRequest.getId())).findFirst().orElseThrow(() -> new NotFoundException("Not found dice"));

        if (roomDice.getIsKeep()) {
            roomDice.setStatus(0);
        } else {
            roomDice.setStatus(1);
        }

        this.sendMessageDices(room);
    }

    public void unKeepDice(@NonNull @DestinationVariable String id, RoomDto.UnKeepDiceRequest unKeepDiceRequest) {
        Room room = this.rooms.stream().filter((x) -> x.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException("Not found Room"));
        RoomUser roomUser = room.getRoomUsers().stream().filter(x -> x.getUuid().equals(unKeepDiceRequest.getUuid())).findFirst().orElseThrow(() -> new NotFoundException("Not found user"));
        room.getRoomTeams().stream().filter(x -> x.getRoomUsers().contains(roomUser)).findFirst().orElseThrow(() -> new ForbiddenException("Can only do your team turn"));
        RoomDice roomDice = room.getRoomDices().stream().filter(x -> x.getId().equals(unKeepDiceRequest.getId())).findFirst().orElseThrow(() -> new NotFoundException("Not found dice"));

        if (roomDice.getIsKeep()) {
            roomDice.setStatus(2);
        } else {
            roomDice.setStatus(0);
        }

        this.sendMessageDices(room);
    }
}
