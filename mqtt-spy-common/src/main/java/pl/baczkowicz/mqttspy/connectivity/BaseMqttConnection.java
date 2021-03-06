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

import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.topicmatching.TopicMatcher;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.utils.ConnectionUtils;
import pl.baczkowicz.mqttspy.utils.TimeUtils;

/**
 * Base MQTT connection class, encapsulating the Paho's MQTT client and providing some common features.
 */
public abstract class BaseMqttConnection implements IMqttConnection
{
	/** Diagnostic logger. */
	private static final Logger logger = LoggerFactory.getLogger(BaseMqttConnection.class);

	/** Number of connection attempts made. */
	private int connectionAttempts = 0;

	/** Last connection attempt timestamp. */
	private long lastConnectionAttemptTimestamp = ConnectionUtils.NEVER_STARTED;
	
	/** Last successful connection attempt timestamp. */
	private Date lastSuccessfulConnectionAttempt;	
	
	/** The Paho MQTT client. */
	protected MqttAsyncClient client;	

	/** Connection options. */
	protected final MqttConnectionDetailsWithOptions connectionDetails;	

	/** COnnection status. */
	private MqttConnectionStatus connectionStatus = MqttConnectionStatus.NOT_CONNECTED;

	/** Disconnection reason (if any). */
	private String disconnectionReason;
	
	/** Used for matching topics to subscriptions. */
	private final TopicMatcher topicMatcher;
	
	/**
	 * Instantiates the BaseMqttConnection.
	 * 
	 * @param connectionDetails Connection details
	 */
	public BaseMqttConnection(final MqttConnectionDetailsWithOptions connectionDetails)
	{
		this.connectionDetails = connectionDetails;
		this.topicMatcher = new TopicMatcher();		
	}
	
	/**
	 * Creates an asynchronous client with the given callback.
	 * 
	 * @param callback The callback to be set on the MQTT client
	 * 
	 * @throws MqttSpyException Thrown when errors detected
	 */
	public void createClient(final MqttCallback callback) throws MqttSpyException
	{
		try
		{
			// Creating MQTT client instance
			client = new MqttAsyncClient(
					connectionDetails.getServerURI().get(0), 
					connectionDetails.getClientID(),
					null);
			
			// Set MQTT callback
			client.setCallback(callback);
		}
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Cannot instantiate the MQTT client", e);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Cannot instantiate the MQTT client", e);
		}
	}
	
	/**
	 * Asynchronous connection attempt.
	 * 
	 * TODO: check if this parameter is needed
	 * @param options The connection options
	 * @param userContext The user context (used for any callbacks)
	 * @param callback Connection result callback
	 * 
	 * @throws MqttSpyException Thrown when errors detected
	 */
	public void connect(final MqttConnectOptions options, final Object userContext, final IMqttActionListener callback) throws MqttSpyException
	{
		recordConnectionAttempt();
		
		try
		{
			client.connect(options, userContext, callback);
		}
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
		catch (MqttSecurityException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
	}
	
	/**
	 * Synchronous connection attempt.
	 * 
	 * TODO: check if this parameter is needed
	 * @param options The connection options
	 * 
	 * @throws MqttSpyException Thrown when errors detected
	 */
	public void connectAndWait(final MqttConnectOptions options) throws MqttSpyException
	{
		recordConnectionAttempt();
		
		try
		{
			client.connect(options).waitForCompletion();
		}
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
		catch (MqttSecurityException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
	}
	
	/**
	 * Records a connection attempt.
	 */
	protected void recordConnectionAttempt()
	{
		lastConnectionAttemptTimestamp = TimeUtils.getMonotonicTime();
		connectionAttempts++;		
	}
	
	/** Records a successful connection. */
	public void recordSuccessfulConnection()
	{
		lastSuccessfulConnectionAttempt = new Date();
	}
	
	/**
	 * Returns last successful connection attempt.
	 * 
	 * @return Formatted date of the last successful connection attempt
	 */
	public String getLastSuccessfulyConnectionAttempt()
	{
		return TimeUtils.DATE_WITH_SECONDS_SDF.format(lastSuccessfulConnectionAttempt);
	}
	
	/**
	 * Subscribes to the given topic and quality of service.
	 * 
	 * @param topic The topic to subscribe to
	 * @param qos The quality of service requested
	 * 
	 * @throws MqttSpyException Thrown when errors detected
	 */
	public void subscribeToTopic(final String topic, final int qos) throws MqttSpyException
	{
		if (client == null || !client.isConnected())
		{
			// TODO: consider throwing an exception here
			logger.warn("Client not connected");
			return;
		}
		
		try
		{
			client.subscribe(topic, qos);
			
			topicMatcher.addSubscriptionToStore(topic);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Subscription attempt failed", e);
		}
	}	
	
	public abstract boolean unsubscribeAll(final boolean manualOverride);

	/**
	 * Checks if a message can be published.
	 * 
	 * @return True if the client is connected	
	 */
	public boolean canPublish()
	{
		return client != null && client.isConnected();
	}
	
	/**
	 * Records lost connection.
	 * 
	 * @param cause The cause of the connection loss
	 */
	public void connectionLost(Throwable cause)
	{
		setDisconnectionReason(cause.getMessage());
		setConnectionStatus(MqttConnectionStatus.DISCONNECTED);
	}
	
	/**
	 * Sets the disconnection reason to the given message.
	 * 
	 * @param message The disconnection reason
	 */
	public void setDisconnectionReason(final String message)
	{
		this.disconnectionReason = message;
		if (!message.isEmpty())
		{
			this.disconnectionReason = this.disconnectionReason + " ("
					+ TimeUtils.DATE_WITH_SECONDS_SDF.format(new Date()) + ")";
		}
	}
	
	public void disconnect()
	{
		try
		{
			client.disconnect(0);
			logger.info("Client {} disconnected", client.getClientId());
		}
		catch (MqttException e)
		{
			logger.error("Cannot disconnect", e);
		}
	}		
	
	// ===============================
	// === Setters and getters =======
	// ===============================
	
	/**
	 * Gets the last disconnection reason.
	 * 
	 * @return The disconnection reason
	 */
	public String getDisconnectionReason()
	{
		return disconnectionReason;
	}
	
	/**
	 * Gets the MQTT connection details.
	 * 
	 * @return The MQTT connection details
	 */
	public MqttConnectionDetailsWithOptions getMqttConnectionDetails()
	{
		return connectionDetails;
	}

	/**
	 * Gets the last connection attempt timestamp.
	 * 
	 * @return Last connection attempt timestamp
	 */
	public long getLastConnectionAttemptTimestamp()
	{
		return lastConnectionAttemptTimestamp;
	}

	/**
	 * Gets the number of connections attempts made so far.
	 * 
	 * @return The number of connection attempts
	 */
	public int getConnectionAttempts()
	{
		return connectionAttempts;
	}
	
	/**
	 * Gets the current connection status.
	 * 
	 * @return Current connection status
	 */
	public MqttConnectionStatus getConnectionStatus()
	{
		return connectionStatus;
	}
	
	/**
	 * Sets the new connection status
	 * 
	 * @param connectionStatus The connection status to set
	 */
	public void setConnectionStatus(final MqttConnectionStatus connectionStatus)
	{
		this.connectionStatus = connectionStatus;
	}

	/**
	 * Gets the topic matcher.
	 * 
	 * @return Topic matcher
	 */
	public TopicMatcher getTopicMatcher()
	{
		return topicMatcher;
	}

	/**
	 * Gets the MQTT client.
	 * 
	 * @return the client
	 */
	public MqttAsyncClient getClient()
	{
		return client;
	}

	/**
	 * Sets the MQTT client - primarily for testing. 
	 * 
	 * @param client the client to set
	 */
	public void setClient(final MqttAsyncClient client)
	{
		this.client = client;
	}
}
