/***********************************************************************************
 * 
 * Copyright (c) 2014 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.mqttspy.connectivity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.connectivity.reconnection.ReconnectionManager;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;

/**
 * MQTT connection with reconnection.
 */
public abstract class MqttConnectionWithReconnection extends BaseMqttConnection
{
	/** Diagnostic logger. */
	private static final Logger logger = LoggerFactory.getLogger(MqttConnectionWithReconnection.class);
	
	/** If reconnection configured, the reconnection manager to be used. */
	private ReconnectionManager reconnectionManager;

	/**
	 * Creates an MqttConnectionWithReconnection.
	 * 
	 * @param reconnectionManager If reconnection configured, the reconnection manager to be used
	 * @param connectionDetails Connection details
	 */
	public MqttConnectionWithReconnection(final ReconnectionManager reconnectionManager, final MqttConnectionDetailsWithOptions connectionDetails)
	{
		super(connectionDetails);
		this.reconnectionManager = reconnectionManager;
	}

	/**
	 * Connects to the specified server(s).
	 * 
	 * @param callback The MQTT callback to set
	 * @param connectionRunnable The runnable that actually performs the connection
	 * 
	 * @throws MqttSpyException Thrown if anything goes wrong
	 */
	public void connect(final MqttCallback callback, final Runnable connectionRunnable) throws MqttSpyException
	{
		createClient(callback);
		
		final ReconnectionSettings reconnectionSettings = getMqttConnectionDetails().getReconnectionSettings();
		
		if (reconnectionSettings == null)
		{
			new Thread(connectionRunnable).start();
		}
		else
		{			
			reconnectionManager.addConnection(this, connectionRunnable);
		}
	}

	/**
	 * Disconnect from the currently connected server.
	 * 
	 * @param diconnectionResultHandler The disconnect result handler to be used
	 * 
	 * @throws MqttSpyException Thrown if anything goes wrong
	 */
	public void disconnect(final IMqttActionListener diconnectionResultHandler) throws MqttSpyException
	{		
		reconnectionManager.removeConnection(this);

		// TODO: check if connected?

		setConnectionStatus(MqttConnectionStatus.DISCONNECTING);
		unsubscribeAll(true);

		try
		{			
			// TODO: turn list into a string
			logger.info("Disconnecting " + getMqttConnectionDetails().getClientID() + " from " + getMqttConnectionDetails().getServerURI());
			if (getClient() != null && getClient().isConnected())
			{
				getClient().disconnect(this, diconnectionResultHandler);
			}
			else
			{
				logger.debug("Already disconnected");
			}
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Cannot disconnect from connection " +  
					getMqttConnectionDetails().getId() + " " + getMqttConnectionDetails().getName(), e);			
		}		
	}
}
