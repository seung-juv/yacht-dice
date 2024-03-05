import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
  Container,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
} from '@mui/material';
import { Lock, LockOpen } from '@mui/icons-material';
import api from '../../apis/api';

export default function Page() {
  const navigate = useNavigate();
  const query = useQuery({
    queryKey: ['ROOM'],
    queryFn() {
      return api.get('/api/v1/room');
    },
  });
  return (
    <Container maxWidth="xs">
      <List sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}>
        {query.data?.data.map((result) => {
          return (
            <ListItem key={`List-${result.id}`} disableGutters>
              <ListItemButton
                onClick={() => {
                  navigate(`/room/${result.id}`);
                }}
              >
                <ListItemText primary={result.title} />
                {result.isSecret ? <Lock /> : <LockOpen />}
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Container>
  );
}
