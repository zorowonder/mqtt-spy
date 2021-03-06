/***********************************************************************************
 * 
 * Copyright (c) 2015 Kamil Baczkowicz
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
package pl.baczkowicz.mqttspy.ui.controllers.edit;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.UserInterfaceMqttConnectionDetails;
import pl.baczkowicz.mqttspy.ui.EditConnectionController;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.KeyboardUtils;

/**
 * Controller for editing a single connection - other/ui tab.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EditConnectionOtherController extends AnchorPane implements Initializable, EditConnectionSubController
{
	/** The parent controller. */
	private EditConnectionController parent;

	// UI & Formatting
		
	@FXML
	private CheckBox autoOpen;
	
	@FXML
	private CheckBox autoConnect;
	
	@FXML
	private CheckBox autoSubscribe;
		
	@FXML
	private TextField maxMessagesStored;
	
	@FXML
	private TextField minMessagesPerTopicStored;
	
	@FXML
	private ComboBox<FormatterDetails> formatter;
	
	private ConfigurationManager configurationManager;

	private final ChangeListener basicOnChangeListener = new ChangeListener()
	{
		@Override
		public void changed(ObservableValue observable, Object oldValue, Object newValue)
		{
			onChange();			
		}		
	};
	
	// ===============================
	// === Initialisation ============
	// ===============================

	public void initialize(URL location, ResourceBundle resources)
	{				
		// UI
		autoConnect.selectedProperty().addListener(basicOnChangeListener);
		autoOpen.selectedProperty().addListener(basicOnChangeListener);
		autoSubscribe.selectedProperty().addListener(basicOnChangeListener);
		
		maxMessagesStored.textProperty().addListener(basicOnChangeListener);
		maxMessagesStored.addEventFilter(KeyEvent.KEY_TYPED, KeyboardUtils.nonNumericKeyConsumer);
		
		minMessagesPerTopicStored.textProperty().addListener(basicOnChangeListener);
		minMessagesPerTopicStored.addEventFilter(KeyEvent.KEY_TYPED, KeyboardUtils.nonNumericKeyConsumer);
		
		formatter.getSelectionModel().selectedIndexProperty().addListener(basicOnChangeListener);
		formatter.setCellFactory(new Callback<ListView<FormatterDetails>, ListCell<FormatterDetails>>()
				{
					@Override
					public ListCell<FormatterDetails> call(ListView<FormatterDetails> l)
					{
						return new ListCell<FormatterDetails>()
						{
							@Override
							protected void updateItem(FormatterDetails item, boolean empty)
							{
								super.updateItem(item, empty);
								if (item == null || empty)
								{
									setText(null);
								}
								else
								{									
									setText(item.getName());
								}
							}
						};
					}
				});
		formatter.setConverter(new StringConverter<FormatterDetails>()
		{
			@Override
			public String toString(FormatterDetails item)
			{
				if (item == null)
				{
					return null;
				}
				else
				{
					return item.getName();
				}
			}

			@Override
			public FormatterDetails fromString(String id)
			{
				return null;
			}
		});
	}

	public void init()
	{
		formatter.getItems().clear();		
		formatter.getItems().add(FormattingUtils.createBasicFormatter("default", 				"Plain", ConversionMethod.PLAIN));
		formatter.getItems().add(FormattingUtils.createBasicFormatter("default-hexDecoder", 	"HEX decoder", ConversionMethod.HEX_DECODE));
		formatter.getItems().add(FormattingUtils.createBasicFormatter("default-hexEncoder", 	"HEX encoder", ConversionMethod.HEX_ENCODE));
		formatter.getItems().add(FormattingUtils.createBasicFormatter("default-base64Decoder", 	"Base64 decoder", ConversionMethod.BASE_64_DECODE));
		formatter.getItems().add(FormattingUtils.createBasicFormatter("default-base64Encoder", 	"Base64 encoder", ConversionMethod.BASE_64_ENCODE));	
		
		// Populate those from the configuration file
		for (final FormatterDetails formatterDetails : configurationManager.getConfiguration().getFormatting().getFormatter())
		{			
			// Make sure the element we're trying to add is not on the list already
			boolean found = false;
			
			for (final FormatterDetails existingFormatterDetails : formatter.getItems())
			{
				if (existingFormatterDetails.getID().equals(formatterDetails.getID()))
				{
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				formatter.getItems().add(formatterDetails);
			}
		}	
	}

	// ===============================
	// === Logic =====================
	// ===============================

	public void onChange()
	{
		parent.onChange();				
	}

	@Override
	public UserInterfaceMqttConnectionDetails readValues(final UserInterfaceMqttConnectionDetails connection)
	{
		connection.setAutoConnect(autoConnect.isSelected());
		connection.setAutoOpen(autoOpen.isSelected());
		connection.setAutoSubscribe(autoSubscribe.isSelected());
		connection.setFormatter(formatter.getSelectionModel().getSelectedItem());
		connection.setMaxMessagesStored(Integer.valueOf(maxMessagesStored.getText()));
		connection.setMinMessagesStoredPerTopic(Integer.valueOf(minMessagesPerTopicStored.getText()));
		
		return connection;
	}
	
	@Override
	public void displayConnectionDetails(final ConfiguredConnectionDetails connection)
	{
		// UI
		autoConnect.setSelected(connection.isAutoConnect() == null ? false : connection.isAutoConnect());
		autoOpen.setSelected(connection.isAutoOpen() == null ? false : connection.isAutoOpen());
		autoSubscribe.setSelected(connection.isAutoSubscribe() == null ? false : connection.isAutoSubscribe());
		maxMessagesStored.setText(connection.getMaxMessagesStored().toString());
		minMessagesPerTopicStored.setText(connection.getMinMessagesStoredPerTopic().toString());
				
		if (formatter.getItems().size() > 0 && connection.getFormatter() != null)
		{
			for (final FormatterDetails item : formatter.getItems())
			{
				if (item.getID().equals(((FormatterDetails) connection.getFormatter()).getID()))
				{
					formatter.getSelectionModel().select(item);
					break;
				}
			}
		}	
		else
		{
			formatter.getSelectionModel().clearSelection();
		}
	}		

	// ===============================
	// === Setters and getters =======
	// ===============================
	
	public void setConfigurationManager(final ConfigurationManager configurationManager)
	{
		this.configurationManager = configurationManager;
	}

	@Override
	public void setParent(final EditConnectionController controller)
	{
		this.parent = controller;
	}
}
