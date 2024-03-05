import React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';

function ErrorDialog({ onClick, open, message }) {
  return (
    <Dialog
      open={open}
      onClose={onClick}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
    >
      <DialogTitle id="alert-dialog-title">Room</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">
          {message}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClick} autoFocus>
          OK
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default ErrorDialog;
