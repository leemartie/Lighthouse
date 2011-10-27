/*******************************************************************************
* Copyright (c) {2009,2011} {Software Design and Collaboration Laboratory (SDCL)
*				, University of California, Irvine}.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    {Software Design and Collaboration Laboratory (SDCL)
*	, University of California, Irvine}
*			- initial API and implementation and/or initial documentation
*******************************************************************************/
package edu.uci.lighthouse.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.uci.lighthouse.core.controller.Controller;
import edu.uci.lighthouse.core.listeners.IPluginListener;
import edu.uci.lighthouse.core.listeners.JavaFileChangedReporter;
import edu.uci.lighthouse.core.listeners.SVNEventReporter;
import edu.uci.lighthouse.core.preferences.DatabasePreferences;
import edu.uci.lighthouse.core.preferences.UserPreferences;
import edu.uci.lighthouse.core.util.SSHTunnel;
import edu.uci.lighthouse.core.util.UserDialog;
import edu.uci.lighthouse.core.util.WorkbenchUtility;
import edu.uci.lighthouse.model.LighthouseAuthor;
import edu.uci.lighthouse.model.jpa.JPAException;
import edu.uci.lighthouse.model.jpa.JPAUtility;
import edu.uci.lighthouse.model.jpa.LHAuthorDAO;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IPropertyChangeListener, IStartup {

	private static Logger logger = Logger.getLogger(Activator.class);
	
	// The plug-in ID
	public static final String PLUGIN_ID = "edu.uci.lighthouse.core"; 

	// The shared instance
	private static Activator plugin;
	
	Collection<IPluginListener> listeners = new LinkedList<IPluginListener>();
	
	private LighthouseAuthor author;
	
//	private SSHTunnel sshTunnel;
	
	/**
	 * The constructor
	 */
	public Activator() {
		Controller controller = new Controller();
		
		JavaFileChangedReporter jReporter = new JavaFileChangedReporter();
		jReporter.addJavaFileStatusListener(controller);
		
		SVNEventReporter svnReporter = new SVNEventReporter();
		svnReporter.addSVNEventListener(controller);
		
		listeners.add(controller);
		listeners.add(jReporter);
		listeners.add(svnReporter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		logger.debug("Core Started");
		
		//This code exist here just for test purposes. We delete the preferences to simulate the behavior when the user first open lighthouse
//		UserPreferences.clear();
//		DatabasePreferences.clear();

		//		sshTunnel = new SSHTunnel(DatabasePreferences.getAllSettings());
//		if (DatabasePreferences.isConnectingUsingSSH()) {
//			sshTunnel.start(context);
//		}
		//FIXME: Think about the right place to put this code
		JPAUtility.initializeEntityManagerFactory(DatabasePreferences.getDatabaseSettings());
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		
		// Starting listeners
		for (IPluginListener listener : listeners) {
			listener.start(context);
		}

		//NEW

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		
		// Stopping listeners
		for (IPluginListener listener : listeners) {
			listener.stop(context);
		}
		
		JPAUtility.shutdownEntityManagerFactory();
		
//		sshTunnel.stop(context);
		
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public LighthouseAuthor getAuthor() throws JPAException{
		if (author == null){
			String userName = getUsername();
			if ("".equals(userName)){
				WorkbenchUtility.openPreferences();
			} else {
				author = new LighthouseAuthor(userName);
			}
		}
		return author;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// Username has changed
		try {
			if (UserPreferences.USERNAME.equals(event.getProperty())) {
				String userName = getUsername();
				if (!"".equals(userName)) {
					LighthouseAuthor user = new LighthouseAuthor(userName);
					new LHAuthorDAO().save(user);
					author = user;
				}
			}
		} catch (JPAException e) {
			UserDialog.openError(e.getMessage());
		}
	}
	
	private String getUsername(){
		String result = "";
		Properties userSettings = UserPreferences.getUserSettings();
		String userName = userSettings.getProperty(UserPreferences.USERNAME);
		if (userName != null) {
			result = userName;
		}
		return result;
	}

	@Override
	public void earlyStartup() {
		logger.debug("CoreStartup earlyStartup...");
	}
}
