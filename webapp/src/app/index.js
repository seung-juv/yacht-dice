import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { v4 as uuid } from 'uuid';
import RootPage from './page';
import RoomPage from './room/page';
import RoomCreatePage from './room/create/page';
import RoomDetailPage from './room/[id]/page';
import Layout from './layout';

function Index() {
  const [init, setInit] = React.useState(false);
  const [cookie, setCookie] = useCookies(['uuid', 'nickname']);

  React.useEffect(() => {
    if (!cookie.uuid) {
      setCookie('uuid', uuid(), {
        path: '/',
      });
    }
    if (!cookie.nickname) {
      setCookie('nickname', 'Yacht Dice', {
        path: '/',
      });
    }
    setInit(true);
  }, [cookie]);

  if (!init) {
    return null;
  }

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<RootPage />} />
        <Route path="/room" element={<RoomPage />} />
        <Route path="/room/create" element={<RoomCreatePage />} />
        <Route path="/room/:id" element={<RoomDetailPage />} />
      </Routes>
    </Layout>
  );
}

export default Index;
