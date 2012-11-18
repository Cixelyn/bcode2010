package lazer2;

import battlecode.common.Message;


public abstract interface MessageHandler {
	public abstract void receivedMessage(Message m);
}
