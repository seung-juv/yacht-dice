import React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
} from '@mui/material';

function JoinDialog({ open, onClose, onSubmit }) {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      PaperProps={{
        component: 'form',
        onSubmit,
      }}
    >
      <DialogTitle>Room</DialogTitle>
      <DialogContent>
        <DialogContentText>Input Password</DialogContentText>
        <TextField
          autoFocus
          required
          margin="dense"
          name="password"
          label="Password"
          type="text"
          fullWidth
          variant="standard"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button type="submit">Join</Button>
      </DialogActions>
    </Dialog>
  );
}

export default JoinDialog;
