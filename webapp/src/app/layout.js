import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  AppBar,
  Box,
  Button,
  Container,
  Divider,
  Drawer,
  MenuItem,
  Toolbar,
  Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';

export default function Layout({ children }) {
  const navigate = useNavigate();
  const [open, setOpen] = React.useState(false);
  return (
    <Container
      component="div"
      maxWidth="lg"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    >
      <AppBar
        position="fixed"
        sx={{
          boxShadow: 0,
          bgcolor: 'transparent',
          backgroundImage: 'none',
          mt: 2,
        }}
      >
        <Container maxWidth="lg">
          <Toolbar
            variant="regular"
            sx={(theme) => ({
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              flexShrink: 0,
              borderRadius: '999px',
              bgcolor:
                theme.palette.mode === 'light'
                  ? 'rgba(255, 255, 255, 0.4)'
                  : 'rgba(0, 0, 0, 0.4)',
              backdropFilter: 'blur(24px)',
              maxHeight: 40,
              border: '1px solid',
              borderColor: 'divider',
              boxShadow:
                theme.palette.mode === 'light'
                  ? `0 0 1px rgba(85, 166, 246, 0.1), 1px 1.5px 2px -1px rgba(85, 166, 246, 0.15), 4px 4px 12px -2.5px rgba(85, 166, 246, 0.15)`
                  : '0 0 1px rgba(2, 31, 59, 0.7), 1px 1.5px 2px -1px rgba(2, 31, 59, 0.65), 4px 4px 12px -2.5px rgba(2, 31, 59, 0.65)',
            })}
          >
            <Box
              sx={{
                flexGrow: 1,
                display: 'flex',
                alignItems: 'center',
                ml: '-18px',
                px: 0,
              }}
            >
              <Typography
                component="h1"
                variant="h5"
                color="text.primary"
                sx={{
                  mx: 3,
                }}
              >
                <Link to="/">Yacht Dice</Link>
              </Typography>
              <Box sx={{ display: { xs: 'none', md: 'flex' } }}>
                <MenuItem
                  sx={{ py: '6px', px: '12px' }}
                  onClick={() => {
                    navigate('/room');
                  }}
                >
                  <Typography variant="body2" color="text.primary">
                    Rooms
                  </Typography>
                </MenuItem>
                <MenuItem
                  sx={{ py: '6px', px: '12px' }}
                  onClick={() => {
                    navigate('/room/create');
                  }}
                >
                  <Typography variant="body2" color="text.primary">
                    Create Room
                  </Typography>
                </MenuItem>
              </Box>
            </Box>
            <Box
              sx={{
                display: { xs: 'none', md: 'flex' },
                gap: 0.5,
                alignItems: 'center',
              }}
            >
              <Button
                color="primary"
                variant="text"
                size="small"
                component={Link}
                to="/auth/sign-in"
              >
                Sign in
              </Button>
              <Button
                color="primary"
                variant="contained"
                size="small"
                component={Link}
                to="/auth/sign-up"
              >
                Sign up
              </Button>
            </Box>
            <Box sx={{ display: { sm: '', md: 'none' } }}>
              <Button
                variant="text"
                color="primary"
                aria-label="menu"
                onClick={() => {
                  setOpen(true);
                }}
                sx={{ minWidth: '30px', p: '4px' }}
              >
                <MenuIcon />
              </Button>
              <Drawer
                anchor="right"
                open={open}
                onClose={() => {
                  setOpen(false);
                }}
              >
                <Box
                  sx={{
                    minWidth: '60dvw',
                    p: 2,
                    backgroundColor: 'background.paper',
                    flexGrow: 1,
                  }}
                >
                  <MenuItem
                    onClick={() => {
                      setOpen(false);
                      navigate('/');
                    }}
                  >
                    <Typography variant="body2" color="text.primary">
                      Home
                    </Typography>
                  </MenuItem>
                  <MenuItem
                    onClick={() => {
                      setOpen(false);
                      navigate('/room');
                    }}
                  >
                    <Typography variant="body2" color="text.primary">
                      Rooms
                    </Typography>
                  </MenuItem>
                  <MenuItem
                    onClick={() => {
                      setOpen(false);
                      navigate('/room/create');
                    }}
                  >
                    <Typography variant="body2" color="text.primary">
                      Create Room
                    </Typography>
                  </MenuItem>
                  <Divider />
                  <MenuItem>
                    <Button
                      color="primary"
                      variant="contained"
                      component="a"
                      href="/material-ui/getting-started/templates/sign-up/"
                      target="_blank"
                      sx={{ width: '100%' }}
                    >
                      Sign up
                    </Button>
                  </MenuItem>
                  <MenuItem>
                    <Button
                      color="primary"
                      variant="outlined"
                      component="a"
                      href="/material-ui/getting-started/templates/sign-in/"
                      target="_blank"
                      sx={{ width: '100%' }}
                    >
                      Sign in
                    </Button>
                  </MenuItem>
                </Box>
              </Drawer>
            </Box>
          </Toolbar>
        </Container>
      </AppBar>
      <Box
        component="main"
        sx={{
          pt: 15,
          pb: 15,
          width: '100%',
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        {children}
      </Box>
    </Container>
  );
}
