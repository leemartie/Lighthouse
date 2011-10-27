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
package edu.uci.lighthouse.model.jpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

/**
 * Utility persistence Class
 */
public class JPAUtility {

	/**
	 * Name of persistence unit.
	 */
	private static final String PERSISTENCE_UNIT = "LighthousePersistenceUnit";

	/**
	 * Factory that create Entity Manager.
	 */
	private static EntityManagerFactory factoryEntityManager;
	
	// For shutdown purposes
	private static Collection<EntityManagerFactory>cacheFactory = new LinkedList<EntityManagerFactory>();
	
	private static Logger logger = Logger.getLogger(JPAUtility.class);
	
	public static void initializeEntityManagerFactory(Properties dbSettings) {
		logger.info("initializing EntityManagerFactory...");
		try {
			EntityManagerFactory factory = Persistence
					.createEntityManagerFactory(PERSISTENCE_UNIT, dbSettings);
			cacheFactory.add(factory);
			// Just change the reference if no RuntimeException is throw
			factoryEntityManager = factory;
		} catch (RuntimeException e) {
			logger.error(e,e);
		}
	}
	
	public static void shutdownEntityManagerFactory() {
		logger.info("shutdown EntityManagerFactory...");
		try {
			for (EntityManagerFactory factory : cacheFactory) {
				if (factory.isOpen()) {
					factory.close();
				}
			}
		} catch (RuntimeException e) {
			logger.error(e,e);
		}
	}
	
	public static EntityManager createEntityManager() throws JPAException {
		if (factoryEntityManager == null || !factoryEntityManager.isOpen()) {
			initializeEntityManagerFactory(new Properties());
		}
		return factoryEntityManager.createEntityManager();
	}
	
	/**
	 * Method that create EntityManager.
	 */
	public static EntityManager createEntityManager(Properties mapDatabaseProperties) {
		if (factoryEntityManager == null || !factoryEntityManager.isOpen()) {
			factoryEntityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT,mapDatabaseProperties);
		}
		return factoryEntityManager.createEntityManager();
	}

	/**
	 * Begin transaction.
	 */
	public static void beginTransaction(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen())
			entityManager.getTransaction().begin();
	}

	/**
	 * Shutdown on Entity Manager.
	 */
	public static void closeEntityManager(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen()) {
			entityManager.close();
		}
	}

	/**
	 * Shutdown on Entity Manager and Factory.
	 */
	public static void shutdownAndCloseFactory(EntityManager entityManager) {
		closeEntityManager(entityManager);
		if (factoryEntityManager != null && factoryEntityManager.isOpen()) {
			factoryEntityManager.close();
		}
	}

	/**
	 * Commit Transaction
	 */
	public static void commitTransaction(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen())
			entityManager.getTransaction().commit();
	}

	/**
	 * Rollback Transaction.
	 */
	public static void rollbackTransaction(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen())
			entityManager.getTransaction().rollback();
	}

	/**
	 * Flush on Entity Manager.
	 */
	public void flush(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen())
			entityManager.flush();
	}

	/**
	 * Clean entity manager.
	 */
	public void clear(EntityManager entityManager) {
		if (entityManager != null && entityManager.isOpen())
			entityManager.clear();
	}
	
	public static void canConnect(Properties dbSettings) throws SQLException {
		String url = dbSettings.getProperty("hibernate.connection.url");
		String username = dbSettings
				.getProperty("hibernate.connection.username");
		String password = dbSettings
				.getProperty("hibernate.connection.password");

		// Class.forName(dbSettings.getProperty("hibernate.connection.driver_class"));
		Connection conn = DriverManager.getConnection(url, username, password);
		conn.close();

	}
}
