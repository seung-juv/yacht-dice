import React from 'react';
import {
  Box,
  Button,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
  Typography,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Controller, useForm } from 'react-hook-form';
import { useCookies } from 'react-cookie';

export default function Page() {
  const [cookie, setCookie] = useCookies(['nickname']);
  const navigate = useNavigate();
  const { control, watch } = useForm({
    defaultValues: {
      nickname: cookie.nickname ?? 'Yacht Dice',
    },
  });
  const nickname = watch('nickname');
  const [joinRoomOpen, setJoinRoomOpen] = React.useState(false);

  React.useEffect(() => {
    setCookie('nickname', nickname, {
      path: '/',
    });
  }, [nickname]);

  return (
    <>
      <Container
        maxWidth="xs"
        sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}
      >
        <Typography component="h1" variant="h5" color="text.primary">
          Yacht Dice
        </Typography>
        <Box component="form" sx={{ mt: 3, width: '100%' }}>
          <Controller
            control={control}
            name="nickname"
            render={({ field }) => {
              return (
                <TextField
                  {...field}
                  margin="normal"
                  required
                  fullWidth
                  label="Nickname"
                  autoFocus
                />
              );
            }}
          />
        </Box>
        <Button
          fullWidth
          variant="text"
          sx={{ mt: 3 }}
          onClick={() => {
            navigate('/room');
          }}
        >
          Rooms
        </Button>
        <Button
          fullWidth
          variant="contained"
          sx={{ mt: 2 }}
          onClick={() => {
            navigate('/room/create');
          }}
        >
          Room Create
        </Button>
        <Button
          fullWidth
          variant="outlined"
          sx={{ mt: 2, mb: 2 }}
          onClick={() => {
            setJoinRoomOpen(true);
          }}
        >
          Join Room
        </Button>
      </Container>
      <Dialog
        open={joinRoomOpen}
        onClose={() => {
          setJoinRoomOpen(false);
        }}
        PaperProps={{
          component: 'form',
          onSubmit: (event) => {
            event.preventDefault();
            navigate(`/room/${event.target.id.value}`);
          },
        }}
      >
        <DialogTitle>Join Room</DialogTitle>
        <DialogContent>
          <DialogContentText>Input Room Id.</DialogContentText>
          <TextField
            autoFocus
            required
            margin="dense"
            name="id"
            label="Room Id"
            type="text"
            fullWidth
            variant="standard"
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => {
              setJoinRoomOpen(false);
            }}
          >
            Cancel
          </Button>
          <Button type="submit">Join</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
