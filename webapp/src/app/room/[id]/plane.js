import { usePlane } from '@react-three/cannon';
import React from 'react';
import {
  TABLE_HEIGHT,
  TABLE_WEIGHT,
  TABLE_WIDTH,
  WALL_COLOR,
} from './constants';

function Plane() {
  const [ref] = usePlane(() => ({
    rotation: [-Math.PI / 2, 0, 0],
    material: {
      friction: 0.1,
    },
    position: [0, 0, 0],
  }));

  return (
    <group ref={ref}>
      <mesh castShadow receiveShadow>
        <planeGeometry args={[TABLE_WIDTH, TABLE_HEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        rotation={[-Math.PI / 2, Math.PI, 0]}
        position={[0, -TABLE_HEIGHT / 2, -TABLE_WEIGHT / 2]}
      >
        <planeGeometry args={[TABLE_WIDTH, TABLE_WEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        rotation={[Math.PI / 2, Math.PI, 0]}
        position={[0, TABLE_HEIGHT / 2, -TABLE_WEIGHT / 2]}
      >
        <planeGeometry args={[TABLE_WIDTH, TABLE_WEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        rotation={[Math.PI / 2, Math.PI / 2, 0]}
        position={[TABLE_WIDTH / 2, 0, -TABLE_WEIGHT / 2]}
      >
        <planeGeometry args={[TABLE_HEIGHT, TABLE_WEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        rotation={[Math.PI / 2, -Math.PI / 2, 0]}
        position={[-TABLE_WIDTH / 2, 0, -TABLE_WEIGHT / 2]}
      >
        <planeGeometry args={[TABLE_HEIGHT, TABLE_WEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        rotation={[0, Math.PI, 0]}
        position={[0, 0, -TABLE_WEIGHT]}
      >
        <planeGeometry args={[TABLE_WIDTH, TABLE_HEIGHT]} />
        <meshStandardMaterial color={WALL_COLOR} />
      </mesh>
    </group>
  );
}

export default Plane;
