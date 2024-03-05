import { useBox } from '@react-three/cannon';
import React from 'react';

function Wall({ WIDTH, HEIGHT, WEIGHT, COLOR, position, rotation }) {
  const [ref] = useBox(
    () => ({
      args: [WIDTH, HEIGHT, WEIGHT],
      position: [position[0], position[1] + HEIGHT / 2, position[2]],
      rotation,
      type: 'Static',
    }),
    React.useRef(null),
  );

  return (
    <group ref={ref}>
      <mesh castShadow receiveShadow rotation={[0, Math.PI, 0]}>
        <planeGeometry args={[WIDTH, HEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
      <mesh castShadow receiveShadow position={[0, 0, WEIGHT]}>
        <planeGeometry args={[WIDTH, HEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        position={[-WIDTH / 2, 0, WEIGHT / 2]}
        rotation={[0, -Math.PI / 2, 0]}
      >
        <planeGeometry args={[WEIGHT, HEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        position={[WIDTH / 2, 0, WEIGHT / 2]}
        rotation={[0, Math.PI / 2, 0]}
      >
        <planeGeometry args={[WEIGHT, HEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
      <mesh
        position={[0, -HEIGHT / 2, WEIGHT / 2]}
        rotation={[-Math.PI / 2, Math.PI, 0]}
      >
        <planeGeometry args={[WIDTH, WEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
      <mesh
        castShadow
        receiveShadow
        position={[0, HEIGHT / 2, WEIGHT / 2]}
        rotation={[-Math.PI / 2, 0, 0]}
      >
        <planeGeometry args={[WIDTH, WEIGHT]} />
        <meshStandardMaterial color={COLOR} />
      </mesh>
    </group>
  );
}

export default Wall;
