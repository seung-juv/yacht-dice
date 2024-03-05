import React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';

function RemoveTeamDialog({ open, onClose, onSubmit }) {
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
        <DialogContentText>Are you sure remove team ?</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button color="warning" type="submit">
          Remove
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default RemoveTeamDialog;
