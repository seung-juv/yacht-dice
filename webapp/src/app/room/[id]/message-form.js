import { Controller, useForm } from 'react-hook-form';
import { Box, IconButton, InputBase } from '@mui/material';
import { Send } from '@mui/icons-material';
import React from 'react';

function MessageForm({ onSubmit }) {
  const { control, handleSubmit, reset } = useForm({
    defaultValues: {
      message: '',
    },
  });
  return (
    <Box
      component="form"
      sx={(theme) => ({
        mt: 2,
        p: '2px 8px',
        display: 'flex',
        alignItems: 'center',
        width: '100%',
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
      onSubmit={handleSubmit((data) => {
        onSubmit(data);
        reset();
      })}
    >
      <Controller
        control={control}
        name="message"
        render={({ field }) => {
          return (
            <InputBase
              {...field}
              sx={{ flex: 1, pl: 1 }}
              placeholder="Message"
              autoComplete="off"
            />
          );
        }}
      />
      <IconButton type="submit" sx={{ p: '10px' }}>
        <Send color="primary" />
      </IconButton>
    </Box>
  );
}

export default MessageForm;
