package com.murphysean.bzrflag.agents;

import java.util.Date;

/**
 * Once you have the basic socket communication working build a really dumb agent. This agent should repeat the following forever:
 * Move forward for 3-8 seconds
 * Turn left about 60 degrees and then start going straight again
 * In addition to this movement your really dumb agent should also shoot every 2 seconds (random between 1.5 and 2.5 seconds) or so.
 */
public class DumbAgent extends AbstractAgent{
	public static final int MOVE_MIN = 3;
	public static final int MOVE_MAX = 8;

	public static final float SHOOT_MIN = 1.5f;
	public static final float SHOOT_MAX = 2.5f;
	/**
	 * If 0 it's moving, if 1 it's turning
	 */
	protected int state;

	protected Date lastShoot;
	protected Date startState;
	protected float shootSeconds;
	protected int movingSeconds;

	public DumbAgent(){
		type = "dumb";
		state = 0;
		startState = new Date();
		lastShoot = new Date();
		desiredSpeed = 1.0f;
		desiredAngularVelocity = 0.0f;
		movingSeconds = MOVE_MIN + (int)(Math.random() * ((MOVE_MAX - MOVE_MIN) + 1));
		shootSeconds = SHOOT_MIN + (float)(Math.random() * (SHOOT_MAX - SHOOT_MIN));
	}

	public void setPositionAngle(float positionX, float positionY, float angle){
		Date now = new Date();
		if(state == 0){
			if((now.getTime() - startState.getTime()) >= (movingSeconds * 1000)){
				startState = now;
				state = 1;
				desiredSpeed = 0.0f;
				desiredAngularVelocity = 1.0f;
				System.out.println("Turning");
			}
		}else{
			if((now.getTime() - startState.getTime()) >= 2500){
				startState = now;
				state = 0;
				desiredSpeed = 1.0f;
				desiredAngularVelocity = 0.0f;
				movingSeconds = MOVE_MIN + (int)(Math.random() * ((MOVE_MAX - MOVE_MIN) + 1));
				System.out.println("Speeding");
			}
		}

		if((now.getTime() - lastShoot.getTime()) >= (shootSeconds * 1000l)){
			lastShoot = now;
			desiredTriggerStatus = true;
		}
	}
}
