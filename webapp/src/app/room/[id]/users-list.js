import React from 'react';
import {
  Box,
  Button,
  Checkbox,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Tab,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Tabs,
  Typography,
} from '@mui/material';
import { useCookies } from 'react-cookie';
import { Add, Close, Person } from '@mui/icons-material';
import RemoveTeamDialog from './remove-team-dialog';

function UsersList({
  id,
  clientRef,
  users,
  teams,
  status,
  tempScore,
  isMyTurn,
  me,
  onAddTeam,
  onJoinTeam,
  onRemoveTeam,
  onGameStart,
}) {
  const [cookie] = useCookies(['uuid', 'nickname']);
  const [removeTeam, setRemoveTeam] = React.useState(null);
  const [removeTeamOpen, setRemoveTeamOpen] = React.useState(false);
  const [tab, setTab] = React.useState('users');
  const selectedTeam = teams.find((team) => {
    return team.id === tab;
  });
  const isChoice = isMyTurn && selectedTeam?.id === me.team?.id;

  return (
    <>
      <Box
        sx={(theme) => ({
          width: 350,
          ml: 2,
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
          display: 'flex',
          flexDirection: 'column',
        })}
      >
        <Tabs
          variant="scrollable"
          value={tab}
          onChange={(event, value) => {
            if (value === 'add') {
              onAddTeam();
              return;
            }
            setTab(value);
          }}
        >
          <Tab
            value="users"
            icon={<Person />}
            aria-label="user"
            sx={{ width: 30 }}
          />
          {teams.map((team, teamIndex) => {
            return (
              <Tab
                key={`Team-${team.id}`}
                value={team.id}
                label={
                  <Box
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                    }}
                  >
                    Team {teamIndex + 1}{' '}
                    {status === 'READY' && (
                      <IconButton
                        onClick={(event) => {
                          event.stopPropagation();
                          setRemoveTeam(team);
                          setRemoveTeamOpen(true);
                        }}
                      >
                        <Close color="error" />
                      </IconButton>
                    )}
                  </Box>
                }
                aria-label={team.name}
              />
            );
          })}
          <Tab value="add" icon={<Add />} aria-label="add" />
        </Tabs>
        <Box sx={{ p: 2, display: 'flex', flexDirection: 'column', flex: 1 }}>
          {selectedTeam && (
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'row',
                alignItems: 'center',
              }}
            >
              {status === 'READY' && (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => {
                    onJoinTeam(selectedTeam);
                  }}
                >
                  Join Team
                </Button>
              )}
            </Box>
          )}
          <List
            sx={{
              overflow: 'auto',
              width: '100%',
              display: 'flex',
              flexDirection: 'column',
              height: 150,
            }}
          >
            {[...users].reverse().map((user) => {
              if (selectedTeam && selectedTeam.id !== user.team?.id) {
                return null;
              }
              const teamIndex = teams.findIndex((team) => {
                return team.id === user.team?.id;
              });
              return (
                <ListItem key={`User-${user.uuid}`} alignItems="flex-start">
                  <ListItemText
                    secondary={
                      <Typography
                        sx={{ display: 'inline' }}
                        component="span"
                        variant="body2"
                        color="text.primary"
                      >
                        {teamIndex > -1 ? `Team ${teamIndex + 1}` : `No Team`} -{' '}
                        {user.name}
                      </Typography>
                    }
                  />
                </ListItem>
              );
            })}
          </List>
          {selectedTeam && (
            <TableContainer
              component="table"
              sx={{
                flex: 1,
                width: '100%',
              }}
            >
              <TableBody>
                {[
                  {
                    key: 'Upper-Section',
                    name: 'Upper Section',
                    dataIndex: 'upperSection',
                    children: [
                      {
                        key: 'Aces',
                        name: 'Aces',
                        dataIndex: 'aces',
                      },
                      {
                        key: 'Twos',
                        name: 'Twos',
                        dataIndex: 'twos',
                      },
                      {
                        key: 'Threes',
                        name: 'Threes',
                        dataIndex: 'threes',
                      },
                      {
                        key: 'Fours',
                        name: 'Fours',
                        dataIndex: 'fours',
                      },
                      {
                        key: 'Fives',
                        name: 'Fives',
                        dataIndex: 'fives',
                      },
                      {
                        key: 'Sixes',
                        name: 'Sixes',
                        dataIndex: 'sixes',
                      },
                    ],
                  },
                  {
                    key: 'Lower-Section',
                    name: 'Lower Section',
                    dataIndex: 'lowerSection',
                    children: [
                      {
                        key: 'Three-Of-A-Kind',
                        name: 'Three-Of-A-Kind',
                        dataIndex: 'threeOfAKind',
                      },
                      {
                        key: 'Four-Of-A-Kind',
                        name: 'Four-Of-A-Kind',
                        dataIndex: 'fourOfAKind',
                      },
                      {
                        key: 'Full-House',
                        name: 'Full House',
                        dataIndex: 'fullHouse',
                      },
                      {
                        key: 'Small-Straight',
                        name: 'Small Straight',
                        dataIndex: 'smallStraight',
                      },
                      {
                        key: 'Large-Straight',
                        name: 'Large Straight',
                        dataIndex: 'largeStraight',
                      },
                      {
                        key: 'Chance',
                        name: 'Chance',
                        dataIndex: 'chance',
                      },
                      {
                        key: 'Yahtzee',
                        name: 'Yahtzee',
                        dataIndex: 'yahtzee',
                      },
                    ],
                  },
                ].map((section) => {
                  return (
                    <React.Fragment key={section.key}>
                      <TableRow
                        sx={{
                          '&:last-child td, &:last-child th': { border: 0 },
                        }}
                      >
                        <TableCell colSpan={3}>{section.name}</TableCell>
                      </TableRow>
                      {section.children.map((child) => {
                        const value =
                          selectedTeam.score[section.dataIndex][
                            child.dataIndex
                          ];
                        return (
                          <TableRow
                            key={`${section.key}-${child.key}`}
                            sx={{
                              '&:last-child td, &:last-child th': { border: 0 },
                            }}
                          >
                            <TableCell align="center">
                              <Checkbox
                                size="small"
                                sx={{ width: 24, height: 24 }}
                                checked={value !== null}
                                onChange={(event) => {
                                  if (event.target.checked) {
                                    clientRef.current.publish({
                                      destination: `/ws/room/${id}/set-score`,
                                      body: JSON.stringify({
                                        uuid: cookie.uuid,
                                        scoreKey: child.dataIndex,
                                      }),
                                    });
                                  }
                                }}
                              />
                            </TableCell>
                            <TableCell align="left">{child.name}</TableCell>
                            <TableCell
                              align="center"
                              sx={{
                                color: value === null ? '#999999' : '#000000',
                              }}
                            >
                              {value ??
                                (isChoice &&
                                  tempScore?.[section.dataIndex][
                                    child.dataIndex
                                  ])}
                            </TableCell>
                          </TableRow>
                        );
                      })}
                    </React.Fragment>
                  );
                })}
              </TableBody>
            </TableContainer>
          )}
          <Box
            sx={{
              mt: 2,
            }}
          >
            <Button
              fullWidth
              variant="contained"
              color="primary"
              onClick={() => {
                onGameStart();
              }}
              disabled={status !== 'READY'}
            >
              Game Start
            </Button>
          </Box>
        </Box>
      </Box>
      <RemoveTeamDialog
        open={removeTeamOpen}
        onClose={() => {
          setRemoveTeamOpen(false);
        }}
        onSubmit={(event) => {
          event.preventDefault();
          onRemoveTeam(removeTeam);
          setRemoveTeamOpen(false);
        }}
      />
    </>
  );
}

export default UsersList;
