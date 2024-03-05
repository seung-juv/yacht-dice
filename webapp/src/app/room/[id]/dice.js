import React from 'react';
import { useGLTF } from '@react-three/drei';
import { useBox } from '@react-three/cannon';
import * as THREE from 'three';
import { DICE_SIZE } from './constants';

useGLTF.preload('/assets/glbs/dice.glb');

const Dice = React.forwardRef(
  ({ position, rotation, color, velocity, onClick }, ref) => {
    const stateRef = React.useRef({
      position,
      rotation,
      velocity,
    });
    const { nodes } = useGLTF('/assets/glbs/dice.glb');
    const [boxRef, boxApi] = useBox(
      () => ({
        args: [DICE_SIZE, DICE_SIZE, DICE_SIZE],
        mass: 1500,
        position,
        linearFactor: [1, 2, 1],
        type: 'Dynamic',
        rotation,
        velocity,
      }),
      React.useRef(null),
    );

    React.useImperativeHandle(ref, () => {
      return {
        ref: boxRef,
        state: stateRef.current,
        api: boxApi,
        getSpeed() {
          return Math.sqrt(
            this.state.velocity[0] ** 2 +
              this.state.velocity[1] ** 2 +
              this.state.velocity[2] ** 2,
          );
        },
        getIsMovement() {
          return this.getSpeed() > 0.05;
        },
        getValue() {
          const [x, y, z] = this.state.rotation;
          const rotatedVectors = {
            top: new THREE.Vector3(0, 1, 0).applyEuler(
              new THREE.Euler(x, y, z),
            ),
            bottom: new THREE.Vector3(0, -1, 0).applyEuler(
              new THREE.Euler(x, y, z),
            ),
            front: new THREE.Vector3(0, 0, 1).applyEuler(
              new THREE.Euler(x, y, z),
            ),
            back: new THREE.Vector3(0, 0, -1).applyEuler(
              new THREE.Euler(x, y, z),
            ),
            right: new THREE.Vector3(1, 0, 0).applyEuler(
              new THREE.Euler(x, y, z),
            ),
            left: new THREE.Vector3(-1, 0, 0).applyEuler(
              new THREE.Euler(x, y, z),
            ),
          };

          const angleToTop = {
            top: rotatedVectors.top.angleTo(new THREE.Vector3(0, 1, 0)),
            bottom: rotatedVectors.bottom.angleTo(new THREE.Vector3(0, 1, 0)),
            front: rotatedVectors.front.angleTo(new THREE.Vector3(0, 1, 0)),
            back: rotatedVectors.back.angleTo(new THREE.Vector3(0, 1, 0)),
            right: rotatedVectors.right.angleTo(new THREE.Vector3(0, 1, 0)),
            left: rotatedVectors.left.angleTo(new THREE.Vector3(0, 1, 0)),
          };

          let minAngle = Number.MAX_VALUE;
          let topFaceValue = 1;
          // eslint-disable-next-line no-restricted-syntax
          for (const face in angleToTop) {
            if (angleToTop[face] < minAngle) {
              minAngle = angleToTop[face];
              topFaceValue = face;
            }
          }
          return {
            top: 6,
            bottom: 1,
            front: 2,
            back: 5,
            right: 3,
            left: 4,
          }[topFaceValue];
        },
      };
    });

    /**
     * Initialize
     */
    React.useEffect(() => {
      const positionUnSubscribe = boxApi.position.subscribe(
        // eslint-disable-next-line no-shadow
        (position) => {
          stateRef.current.position = position;
        },
      );
      const rotationUnSubscribe = boxApi.rotation.subscribe(
        // eslint-disable-next-line no-shadow
        (rotation) => {
          stateRef.current.rotation = rotation;
        },
      );
      const velocityUnSubscribe = boxApi.velocity.subscribe(
        // eslint-disable-next-line no-shadow
        (velocity) => {
          stateRef.current.velocity = velocity;
        },
      );

      return () => {
        positionUnSubscribe();
        rotationUnSubscribe();
        velocityUnSubscribe();
      };
    }, []);

    /**
     * Position 이 변경 되었을때 Set Position
     */
    React.useEffect(() => {
      if (position) {
        boxApi.position.set(...position);
      }
    }, [position]);

    /**
     * Rotation 이 변경 되었을때 Set Rotation
     */
    React.useEffect(() => {
      if (rotation) {
        boxApi.rotation.set(...rotation);
      }
    }, [rotation]);

    return (
      <group
        ref={boxRef}
        onClick={(event) => {
          event.stopPropagation();
          onClick(boxApi, event);
        }}
      >
        <mesh castShadow receiveShadow geometry={nodes.Cube_1.geometry}>
          <meshStandardMaterial color="#000000" />
        </mesh>
        <mesh castShadow receiveShadow geometry={nodes.Cube_2.geometry}>
          <meshStandardMaterial color={color} />
        </mesh>
      </group>
    );
  },
);

export default Dice;
