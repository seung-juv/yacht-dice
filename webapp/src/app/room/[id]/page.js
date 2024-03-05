import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Box, CircularProgress } from '@mui/material';
import api from '../../../apis/api';
import ErrorDialog from './error-dialog';
import JoinDialog from './join-dialog';
import Game from './game';

export default function Page() {
  const navigate = useNavigate();
  const location = useLocation();
  const [password, setPassword] = React.useState(
    location.state?.password ?? '',
  );
  const params = useParams();
  const query = useQuery({
    retry: false,
    queryKey: [
      'Room',
      params.id,
      {
        password,
      },
    ],
    queryFn() {
      return api.get(`/api/v1/room/${params.id}`, {
        params: {
          password,
        },
      });
    },
  });

  return (
    <>
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          pb: 2,
        }}
      >
        {query.data ? (
          <Game
            {...query.data.data}
            diceRollCount={query.data.data.currentDiceRollCount}
          />
        ) : (
          <CircularProgress />
        )}
      </Box>
      <JoinDialog
        open={
          query.isError && !password && query.error?.response.status === 403
        }
        onClose={() => {
          navigate(-1);
        }}
        onSubmit={(event) => {
          event.preventDefault();
          setPassword(event.target.password.value);
        }}
      />
      <ErrorDialog
        open={
          query.isError && !!password && query.error?.response.status === 403
        }
        message={query.error?.response.data.message}
        onClick={() => {
          setPassword('');
        }}
      />
      <ErrorDialog
        open={query.isError && query.error?.response.status !== 403}
        message={query.error?.response.data.message}
        onClick={() => {
          navigate(-1);
        }}
      />
    </>
  );
}
