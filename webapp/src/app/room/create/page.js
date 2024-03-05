import React from 'react';
import { useSnackbar } from 'notistack';
import { useCookies } from 'react-cookie';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { Controller, useForm } from 'react-hook-form';
import {
  Box,
  Button,
  Checkbox,
  FormControlLabel,
  TextField,
  Typography,
} from '@mui/material';
import api from '../../../apis/api';

export default function Page() {
  const [cookie] = useCookies(['uuid', 'nickname']);
  const { enqueueSnackbar } = useSnackbar();
  const { watch, handleSubmit, control } = useForm({
    defaultValues: {
      title: '',
      isSecret: false,
      secretPassword: '',
    },
  });
  const mutation = useMutation({
    mutationFn(data) {
      return api.post('/api/v1/room', {
        ...data,
        user: {
          uuid: cookie.uuid,
          name: cookie.nickname,
        },
      });
    },
  });
  const navigate = useNavigate();

  return (
    <Box
      sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}
    >
      <Typography component="h1" variant="h5" color="text.primary">
        Room Create
      </Typography>
      <Box
        component="form"
        onSubmit={handleSubmit((data) => {
          mutation.mutate(data, {
            onSuccess(responseData) {
              enqueueSnackbar('Created !', {
                variant: 'success',
              });
              navigate(`/room/${responseData.data.id}`, {
                state: {
                  password: data.secretPassword,
                },
              });
            },
            onError(error) {
              enqueueSnackbar(error?.response?.data?.message, {
                variant: 'error',
              });
            },
          });
        })}
        sx={{ mt: 1 }}
      >
        <Controller
          control={control}
          name="title"
          render={({ field }) => {
            return (
              <TextField
                {...field}
                margin="normal"
                required
                fullWidth
                label="Title"
                autoFocus
              />
            );
          }}
        />
        <FormControlLabel
          control={
            <Controller
              control={control}
              name="isSecret"
              render={({ field }) => {
                return <Checkbox {...field} color="primary" />;
              }}
            />
          }
          label="Secret"
        />
        <Controller
          control={control}
          name="secretPassword"
          render={({ field }) => {
            return (
              <TextField
                {...field}
                margin="normal"
                required={watch('isSecret')}
                fullWidth
                name="password"
                label="Password"
                type="password"
                autoFocus
                disabled={!watch('isSecret')}
              />
            );
          }}
        />
        <Button
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
        >
          Create
        </Button>
      </Box>
    </Box>
  );
}
