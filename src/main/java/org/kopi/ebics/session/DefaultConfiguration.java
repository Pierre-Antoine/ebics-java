/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.Configuration;
import org.kopi.ebics.interfaces.EbicsLogger;
import org.kopi.ebics.interfaces.EbicsUser;
import org.kopi.ebics.interfaces.LetterManager;
import org.kopi.ebics.interfaces.SerializationManager;
import org.kopi.ebics.interfaces.TraceManager;
import org.kopi.ebics.io.IOUtils;
import org.kopi.ebics.letter.DefaultLetterManager;


/**
 * A simple client application configuration.
 *
 * @author hachani
 *
 */
public class DefaultConfiguration implements Configuration {

  /**
   * Creates a new application configuration.
   * @param rootDir the root directory
   */
  public DefaultConfiguration(String rootDir) {
    this.rootDir = rootDir;
    bundle = ResourceBundle.getBundle(RESOURCE_DIR);
    properties = new Properties();
    logger = new DefaultEbicsLogger();
    serializationManager = new DefaultSerializationManager();
    serializationManager.setSerializationDirectory(rootDir + File.separator + getString("serialization.dir.name"));
    traceManager = new DefaultTraceManager();
  }

  /**
   * Creates a new application configuration.
   * The root directory will be user.home/ebics/client
   */
  public DefaultConfiguration() {
    this(System.getProperty("user.home") + File.separator + "ebics" + File.separator + "client");
  }

  /**
   * Returns the corresponding property of the given key
   * @param key the property key
   * @return the property value.
   */
  private String getString(String key) {
    try {
      return bundle.getString(key);
    } catch(MissingResourceException e) {
      return "!!" + key + "!!";
    }
  }

  /**
   * Loads the configuration
   * @throws EbicsException
   */
  public void load(String configFile) throws EbicsException {
    if (isConfigFileLoad) {
      return;
    }

    try {
      properties.load(new FileInputStream(new File(configFile)));
    } catch (IOException e) {
      throw new EbicsException(e.getMessage());
    }

    isConfigFileLoad = true;
  }

  public String getRootDirectory() {
    return rootDir;
  }

  public void init() {
    //Create the root directory
    IOUtils.createDirectories(getRootDirectory());
    //Create the logs directory
    IOUtils.createDirectories(getLogDirectory());
    //Create the serialization directory
    IOUtils.createDirectories(getSerializationDirectory());
    //create the SSL trusted stores directories
    IOUtils.createDirectories(getSSLTrustedStoreDirectory());
    //create the SSL key stores directories
    IOUtils.createDirectories(getSSLKeyStoreDirectory());
    //Create the SSL bank certificates directories
    IOUtils.createDirectories(getSSLBankCertificates());
    //Create users directory
    IOUtils.createDirectories(getUsersDirectory());

    logger.setLogFile(getLogDirectory() + File.separator + getLogFileName());
    ((DefaultEbicsLogger)logger).setFileLoggingEnabled(true);
    ((DefaultEbicsLogger)logger).setLevel(DefaultEbicsLogger.ALL_LEVEL);
    serializationManager.setSerializationDirectory(getSerializationDirectory());
    traceManager.setTraceEnabled(isTraceEnabled());
    letterManager = new DefaultLetterManager(getLocale());
  }

  
  public Locale getLocale() {
    return Locale.FRANCE;
  }

  
  public String getLogDirectory() {
    return rootDir + File.separator + getString("log.dir.name");
  }

  
  public String getLogFileName() {
    return getString("log.file.name");
  }

  
  public String getConfigurationFile() {
    return rootDir + File.separator + getString("conf.file.name");
  }

  
  public String getProperty(String key) {
    if (!isConfigFileLoad) {
      return null;
    }

    if (key == null) {
      return null;
    }

    return properties.getProperty(key);
  }

  
  public String getKeystoreDirectory(EbicsUser user) {
    return getUserDirectory(user) + File.separator + getString("keystore.dir.name");
  }

  
  public String getTransferTraceDirectory(EbicsUser user) {
    return getUserDirectory(user) + File.separator + getString("traces.dir.name");
  }

  
  public String getSerializationDirectory() {
    return rootDir + File.separator + getString("serialization.dir.name");
  }

  
  public String getSSLTrustedStoreDirectory() {
    return rootDir + File.separator + getString("ssltruststore.dir.name");
  }

  
  public String getSSLKeyStoreDirectory() {
    return rootDir + File.separator + getString("sslkeystore.dir.name");
  }

  
  public String getSSLBankCertificates() {
    return rootDir + File.separator + getString("sslbankcert.dir.name");
  }

  
  public String getUsersDirectory() {
    return rootDir + File.separator + getString("users.dir.name");
  }

  
  public SerializationManager getSerializationManager() {
    return serializationManager;
  }

  
  public TraceManager getTraceManager() {
    return traceManager;
  }

  
  public LetterManager getLetterManager() {
    return letterManager;
  }

  
  public String getLettersDirectory(EbicsUser user) {
    return getUserDirectory(user) + File.separator + getString("letters.dir.name");
  }

  
  public String getUserDirectory(EbicsUser user) {
    return getUsersDirectory() + File.separator + user.getUserId();
  }

  
  public EbicsLogger getLogger() {
    return logger;
  }

  
  public String getSignatureVersion() {
    return getString("signature.version");
  }

  
  public String getAuthenticationVersion() {
    return getString("authentication.version");
  }

  
  public String getEncryptionVersion() {
    return getString("encryption.version");
  }

  
  public boolean isTraceEnabled() {
    return true;
  }

  
  public boolean isCompressionEnabled() {
    return true;
  }

  
  public int getRevision() {
    return 1;
  }

  
  public String getVersion() {
    return getString("ebics.version");
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final String				rootDir;
  private ResourceBundle			bundle;
  private Properties				properties;
  private EbicsLogger				logger;
  private SerializationManager			serializationManager;
  private TraceManager				traceManager;
  private LetterManager				letterManager;
  private boolean				isConfigFileLoad;

  private static final String			RESOURCE_DIR = "org.kopi.ebics.client.config";
}
