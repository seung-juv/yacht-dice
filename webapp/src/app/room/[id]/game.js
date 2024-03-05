import React from 'react';
import {
  Box,
  Button,
  List,
  ListItem,
  ListItemText,
  Typography,
} from '@mui/material';
import { Lock, LockOpen } from '@mui/icons-material';
import { Canvas } from '@react-three/fiber';
import { OrbitControls } from '@react-three/drei';
import { Physics } from '@react-three/cannon';
import * as StompJs from '@stomp/stompjs';
import { useCookies } from 'react-cookie';
import Plane from './plane';
import Wall from './wall';
import {
  DICE_LENGTH,
  DICE_SIZE,
  KEEP_TABLE_DICE_OFFSET,
  KEEP_TABLE_HEIGHT,
  KEEP_TABLE_WIDTH,
  MAX_DICE_ROLL_COUNT,
  TABLE_HEIGHT,
  TABLE_WEIGHT,
  TABLE_WIDTH,
  WALL_COLOR,
  WALL_HEIGHT,
  WALL_WEIGHT,
} from './constants';
import Dice from './dice';
import MessageForm from './message-form';
import UsersList from './users-list';

function Game({
  id = '',
  title = '',
  isSecret = false,
  status: initialStatus,
  users: initialUsers = [],
  messages: initialMessages = [],
  teams: initialTeams = [],
  diceRollCount: initialDiceRollCount,
  progressRoomTeam: initialProgressRommTeam,
  tempScore: initialTempScore,
  dices: initialDices,
}) {
  const [status, setStatus] = React.useState(initialStatus);
  const [cookie] = useCookies(['uuid', 'nickname']);
  const [users, setUsers] = React.useState(initialUsers);
  const me = users.find((user) => {
    return user.uuid === cookie.uuid;
  });
  const [messages, setMessages] = React.useState(initialMessages);
  const [teams, setTeams] = React.useState(initialTeams);
  const dicePositionIntervalRef = React.useRef();
  const diceRefs = React.useRef([...new Array(DICE_LENGTH)]);
  const [diceRollCount, setDiceRollCount] =
    React.useState(initialDiceRollCount);
  const [progressRoomTeam, setProgressRoomTeam] = React.useState(
    initialProgressRommTeam,
  );
  const [isMovement, setIsMovement] = React.useState(false);
  const [tempScore, setTempScore] = React.useState(initialTempScore);
  const [dices, setDices] = React.useState(initialDices);
  const isMyTurn = progressRoomTeam?.id === me.team?.id;
  const clientRef = React.useRef(
    new StompJs.Client({
      brokerURL: 'ws://localhost:8080/ws/room',
      onConnect() {
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/status`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setStatus(data.status);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/chat`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setMessages((prevState) => {
              return [...prevState, data];
            });
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/users`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setUsers(data);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/teams`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setTeams(data);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/turn`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setProgressRoomTeam(data.progressRoomTeam);
            setDiceRollCount(data.currentDiceRollCount);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/dices`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setDices(data);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/dice-value`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setTempScore(data.score);
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/set-score`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setTeams(data.teams);
            diceRefs.current.forEach((diceRef, diceRefIndex) => {
              diceRefs.current[diceRefIndex].isKeep = false;
            });
          },
        );
        clientRef.current.subscribe(
          `/ws/room/subscribe/${id}/dice-roll`,
          (message) => {
            // eslint-disable-next-line no-shadow
            const data = JSON.parse(message.body);
            setDiceRollCount(data.currentDiceRollCount);
            data.dices.forEach((dice, diceIndex) => {
              diceRefs.current[diceIndex].ref.api.position.set(
                dice.position.x,
                dice.position.y,
                dice.position.z,
              );
              diceRefs.current[diceIndex].ref.api.rotation.set(
                dice.rotation.x,
                dice.rotation.y,
                dice.rotation.z,
              );
            });
            if (data.status === 'PLAYING' && cookie.uuid === data.uuid) {
              setIsMovement(true);
              dicePositionIntervalRef.current = setInterval(() => {
                if (
                  !diceRefs.current.some((diceRef) => {
                    return !diceRef.isKeep && diceRef.ref.getIsMovement();
                  })
                ) {
                  setIsMovement(false);
                  clientRef.current.publish({
                    destination: `/ws/room/${id}/set-dice-value`,
                    body: JSON.stringify({
                      uuid: cookie.uuid,
                      dices: diceRefs.current.map((diceRef) => {
                        return {
                          id: dices[diceRef.index].id,
                          value: diceRef.ref.getValue(),
                          position: {
                            x: diceRef.ref.state.position[0],
                            y: diceRef.ref.state.position[1],
                            z: diceRef.ref.state.position[2],
                          },
                          rotation: {
                            x: diceRef.ref.state.rotation[0],
                            y: diceRef.ref.state.rotation[1],
                            z: diceRef.ref.state.rotation[2],
                          },
                        };
                      }),
                    }),
                  });
                  clearInterval(dicePositionIntervalRef.current);
                } else {
                  setIsMovement(true);
                }
              }, 500);
            }
          },
        );
      },
    }),
  );

  function handleDiceRoll() {
    if (isMovement) {
      return;
    }
    if (diceRollCount === MAX_DICE_ROLL_COUNT) {
      return;
    }
    const [keepDices, unKeepDices] = diceRefs.current.reduce(
      (previousValue, currentValue, currentIndex) => {
        const dice = dices[currentIndex];
        if (
          (dice.isKeep && dice.status === 0) ||
          (!dice.isKeep && dice.status === 1)
        ) {
          previousValue[0].push(currentValue);
        } else {
          previousValue[1].push(currentValue);
        }
        return previousValue;
      },
      [[], []],
    );

    const dicesRequest = [];
    keepDices
      .sort((a, b) => a.ref.getValue() - b.ref.getValue())
      .forEach((keepDice, keepDiceIndex) => {
        const dice = {
          id: dices[keepDice.index].id,
        };
        // Set Position
        {
          const x =
            -TABLE_WIDTH / 2 -
            KEEP_TABLE_HEIGHT +
            KEEP_TABLE_DICE_OFFSET +
            DICE_SIZE;
          const y = 1;
          const z =
            -KEEP_TABLE_WIDTH / 2 +
            WALL_WEIGHT +
            DICE_SIZE / 2 +
            KEEP_TABLE_DICE_OFFSET +
            keepDiceIndex *
              (WALL_WEIGHT + KEEP_TABLE_DICE_OFFSET * 2 + DICE_SIZE);
          dice.position = { x, y, z };
        }
        // Set Rotation
        {
          const [x, y, z] = [
            [0, 0, Math.PI],
            [Math.PI * 1.5, 0, 0],
            [Math.PI, Math.PI * 1.5, Math.PI * 1.5],
            [0, 0, Math.PI * 1.5],
            [-Math.PI * 1.5, 0, 0],
            [0, 0, 0],
          ][keepDice.ref.getValue() - 1];
          dice.rotation = { x, y, z };
        }
        dicesRequest[keepDice.index] = dice;
      });
    unKeepDices.forEach((unKeepDice, unKeepDiceIndex) => {
      const dice = {
        id: dices[unKeepDice.index].id,
      };
      const minX =
        -TABLE_WIDTH / 2 + (DICE_SIZE + WALL_WEIGHT) + TABLE_WIDTH / 2;
      const maxX =
        TABLE_WIDTH / 2 - (DICE_SIZE + WALL_WEIGHT) - TABLE_WIDTH / 2;
      const minZ =
        -TABLE_HEIGHT / 2 + (DICE_SIZE + WALL_WEIGHT) + TABLE_WIDTH / 2;
      const maxZ =
        TABLE_HEIGHT / 2 - (DICE_SIZE - WALL_WEIGHT) - TABLE_WIDTH / 2;
      const x = Math.random() * (maxX - minX + 1) + minX;
      const y = 25 + unKeepDiceIndex * DICE_SIZE;
      const z = Math.random() * (maxZ - minZ + 1) + minZ;
      dice.position = {
        x,
        y,
        z,
      };
      dice.rotation = {
        x: Math.random() * Math.PI,
        y: Math.random() * Math.PI,
        z: Math.random() * Math.PI,
      };
      dicesRequest[unKeepDice.index] = dice;
    });

    clientRef.current.publish({
      destination: `/ws/room/${id}/dice-roll`,
      body: JSON.stringify({
        uuid: cookie.uuid,
        dices: dicesRequest,
      }),
    });
  }

  /**
   * Init StompJS
   */
  React.useEffect(() => {
    clientRef.current.activate();
    return () => {
      clientRef.current?.deactivate();
    };
  }, []);

  /**
   * 1초마다 온라인 중인지 체크하는 로직
   */
  React.useEffect(() => {
    const onlineInterval = setInterval(() => {
      clientRef.current.publish({
        destination: `/ws/room/${id}/online`,
        body: JSON.stringify({
          uuid: cookie.uuid,
          roomId: id,
          name: cookie.nickname,
        }),
      });
    }, 1000);
    return () => {
      if (onlineInterval) {
        clearInterval(onlineInterval);
      }
    };
  }, [cookie.uuid]);

  /**
   * dicePositionInterval 존재 시 클리어
   */
  React.useEffect(() => {
    return () => {
      if (dicePositionIntervalRef.current) {
        clearInterval(dicePositionIntervalRef.current);
      }
    };
  }, []);

  return (
    <Box
      sx={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        width: '100%',
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <Typography component="h1" variant="h5" color="text.primary">
          {title}
        </Typography>
        {isSecret ? <Lock /> : <LockOpen />}
      </Box>
      <Box
        sx={{
          mt: 2,
          flex: 1,
          display: 'flex',
          width: '100%',
        }}
      >
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Box
            sx={(theme) => ({
              flex: 1,
              borderRadius: '8px',
              bgcolor:
                theme.palette.mode === 'light'
                  ? 'rgba(255, 255, 255, 0.4)'
                  : 'rgba(0, 0, 0, 0.4)',
              backdropFilter: 'blur(24px)',
              border: '1px solid',
              borderColor: 'divider',
              boxShadow:
                theme.palette.mode === 'light'
                  ? `0 0 1px rgba(85, 166, 246, 0.1), 1px 1.5px 2px -1px rgba(85, 166, 246, 0.15), 4px 4px 12px -2.5px rgba(85, 166, 246, 0.15)`
                  : '0 0 1px rgba(2, 31, 59, 0.7), 1px 1.5px 2px -1px rgba(2, 31, 59, 0.65), 4px 4px 12px -2.5px rgba(2, 31, 59, 0.65)',
              overflow: 'hidden',
              position: 'relative',
              userSelect: 'none',
            })}
          >
            <Canvas
              shadows
              camera={{
                fov: 50,
                position: [1, 35, 10],
              }}
            >
              <OrbitControls />
              <color attach="background" args={['#171720']} />
              <ambientLight intensity={0.5 * Math.PI} />
              <pointLight
                position={[-10, -10, -10]}
                intensity={Math.PI}
                decay={0}
              />
              <spotLight
                position={[25, 25, 25]}
                angle={0.8}
                penumbra={1}
                intensity={Math.PI}
                decay={0}
                castShadow
              />
              <Physics gravity={[0, -80, 0]} allowSleep={false}>
                <Plane />
                <Wall
                  WIDTH={TABLE_WIDTH}
                  HEIGHT={WALL_HEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={WALL_WEIGHT}
                  position={[0, 0, -TABLE_HEIGHT / 2]}
                  rotation={[0, 0, 0]}
                />
                <Wall
                  WIDTH={TABLE_HEIGHT}
                  HEIGHT={WALL_HEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={WALL_WEIGHT}
                  position={[TABLE_WIDTH / 2 - WALL_WEIGHT, 0, 0]}
                  rotation={[0, Math.PI / 2, 0]}
                />
                <Wall
                  WIDTH={TABLE_WIDTH}
                  HEIGHT={WALL_HEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={WALL_WEIGHT}
                  position={[0, 0, TABLE_HEIGHT / 2 - WALL_WEIGHT]}
                  rotation={[0, 0, 0]}
                />
                <Wall
                  WIDTH={TABLE_HEIGHT}
                  HEIGHT={WALL_HEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={WALL_WEIGHT}
                  position={[-TABLE_WIDTH / 2, 0, 0]}
                  rotation={[0, Math.PI / 2, 0]}
                />
                <Wall
                  WIDTH={WALL_WEIGHT}
                  HEIGHT={WALL_HEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={KEEP_TABLE_WIDTH}
                  position={[
                    -TABLE_WIDTH / 2 -
                      WALL_WEIGHT / 2 -
                      DICE_SIZE -
                      KEEP_TABLE_DICE_OFFSET * 2,
                    0,
                    -KEEP_TABLE_WIDTH / 2,
                  ]}
                  rotation={[0, 0, 0]}
                />
                {[...new Array(DICE_LENGTH + 1)].map((_, index) => {
                  return (
                    <Wall
                      // eslint-disable-next-line react/no-array-index-key
                      key={`Keep-Table-Wall-${index}`}
                      WIDTH={KEEP_TABLE_HEIGHT}
                      HEIGHT={WALL_HEIGHT}
                      COLOR={WALL_COLOR}
                      WEIGHT={WALL_WEIGHT}
                      position={[
                        -TABLE_WIDTH / 2 - KEEP_TABLE_HEIGHT / 2 + WALL_WEIGHT,
                        0,
                        KEEP_TABLE_WIDTH / 2 -
                          WALL_WEIGHT -
                          index *
                            (DICE_SIZE +
                              KEEP_TABLE_DICE_OFFSET * 2 +
                              WALL_WEIGHT),
                      ]}
                      rotation={[0, 0, 0]}
                    />
                  );
                })}
                <Wall
                  WIDTH={KEEP_TABLE_WIDTH}
                  HEIGHT={TABLE_WEIGHT}
                  COLOR={WALL_COLOR}
                  WEIGHT={KEEP_TABLE_HEIGHT}
                  position={[
                    -TABLE_WIDTH / 2 +
                      WALL_WEIGHT / 2 -
                      KEEP_TABLE_HEIGHT +
                      WALL_WEIGHT,
                    0,
                    0,
                  ]}
                  rotation={[0, Math.PI / 2, 0]}
                />
                {dices.map((dice, index) => {
                  const color = ['#FFFFFF', '#77AAFF', '#FF77AA'][dice.status];
                  const position = React.useMemo(() => {
                    return [dice.position.x, dice.position.y, dice.position.z];
                  }, [dice.position]);
                  const rotation = React.useMemo(() => {
                    return [dice.rotation.x, dice.rotation.y, dice.rotation.z];
                  }, [dice.rotation]);

                  function handleClick() {
                    if (
                      (dice.isKeep && dice.status === 0) ||
                      (!dice.isKeep && dice.status === 1)
                    ) {
                      clientRef.current.publish({
                        destination: `/ws/room/${id}/un-keep-dice`,
                        body: JSON.stringify({
                          uuid: cookie.uuid,
                          id: dice.id,
                        }),
                      });
                    } else {
                      clientRef.current.publish({
                        destination: `/ws/room/${id}/keep-dice`,
                        body: JSON.stringify({
                          uuid: cookie.uuid,
                          id: dice.id,
                        }),
                      });
                    }
                  }

                  return (
                    <Dice
                      key={`Dice-${dice.id}`}
                      ref={(ref) => {
                        if (!diceRefs.current[index]) {
                          diceRefs.current[index] = {
                            index,
                            ref,
                          };
                        }
                      }}
                      color={color}
                      onClick={handleClick}
                      position={position}
                      rotation={rotation}
                    />
                  );
                })}
              </Physics>
            </Canvas>
            <Box
              sx={{
                width: '100%',
                position: 'absolute',
                bottom: 15,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 2,
              }}
            >
              {(status === 'READY' || isMyTurn) && (
                <Button
                  variant="contained"
                  color={
                    diceRollCount === MAX_DICE_ROLL_COUNT ? 'error' : 'primary'
                  }
                  disabled={isMovement}
                  sx={{
                    cursor:
                      diceRollCount === MAX_DICE_ROLL_COUNT
                        ? 'not-allowed'
                        : 'pointer',
                  }}
                  onClick={() => {
                    handleDiceRoll();
                  }}
                >
                  Dice Roll !
                  {status === 'PLAYING' &&
                    ` (${diceRollCount} / ${MAX_DICE_ROLL_COUNT})`}
                </Button>
              )}
            </Box>
          </Box>
          <Box
            sx={(theme) => ({
              mt: 2,
              borderRadius: '8px',
              bgcolor:
                theme.palette.mode === 'light'
                  ? 'rgba(255, 255, 255, 0.4)'
                  : 'rgba(0, 0, 0, 0.4)',
              backdropFilter: 'blur(24px)',
              border: '1px solid',
              borderColor: 'divider',
              boxShadow:
                theme.palette.mode === 'light'
                  ? `0 0 1px rgba(85, 166, 246, 0.1), 1px 1.5px 2px -1px rgba(85, 166, 246, 0.15), 4px 4px 12px -2.5px rgba(85, 166, 246, 0.15)`
                  : '0 0 1px rgba(2, 31, 59, 0.7), 1px 1.5px 2px -1px rgba(2, 31, 59, 0.65), 4px 4px 12px -2.5px rgba(2, 31, 59, 0.65)',
              overflow: 'hidden',
            })}
          >
            <List
              sx={{
                height: 150,
                overflow: 'auto',
                width: '100%',
                bgcolor: 'background.paper',
                display: 'flex',
                flexDirection: 'column-reverse',
              }}
            >
              {[...messages].reverse().map((message, messageIndex) => {
                return (
                  <ListItem
                    // eslint-disable-next-line react/no-array-index-key
                    key={`Message-${messageIndex}`}
                    alignItems="flex-start"
                    sx={{ py: 0 }}
                  >
                    <ListItemText
                      secondary={
                        <>
                          <Typography
                            sx={{ display: 'inline' }}
                            component="span"
                            variant="body2"
                            color="text.primary"
                          >
                            {message.name}
                          </Typography>
                          {` — `}
                          {message.message}
                        </>
                      }
                    />
                  </ListItem>
                );
              })}
            </List>
          </Box>
          <MessageForm
            onSubmit={(data) => {
              clientRef.current.publish({
                destination: `/ws/room/${id}/chat`,
                body: JSON.stringify({
                  name: cookie.nickname,
                  message: data.message,
                }),
              });
            }}
          />
        </Box>
        <UsersList
          id={id}
          clientRef={clientRef}
          users={users}
          teams={teams}
          status={status}
          tempScore={tempScore}
          isMyTurn={isMyTurn}
          me={me}
          onAddTeam={() => {
            clientRef.current.publish({
              destination: `/ws/room/${id}/add-team`,
            });
          }}
          onJoinTeam={(team) => {
            clientRef.current.publish({
              destination: `/ws/room/${id}/join-team`,
              body: JSON.stringify({
                uuid: cookie.uuid,
                teamId: team.id,
              }),
            });
          }}
          onRemoveTeam={(team) => {
            clientRef.current.publish({
              destination: `/ws/room/${id}/remove-team`,
              body: JSON.stringify({
                uuid: cookie.uuid,
                teamId: team.id,
              }),
            });
          }}
          onGameStart={() => {
            clientRef.current.publish({
              destination: `/ws/room/${id}/play`,
              body: JSON.stringify({
                uuid: cookie.uuid,
              }),
            });
          }}
        />
      </Box>
    </Box>
  );
}

export default Game;
