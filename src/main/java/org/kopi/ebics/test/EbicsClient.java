/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL
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
 * $Id:$
 */

package org.kopi.ebics.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Hashtable;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.kopi.ebics.client.Bank;
import org.kopi.ebics.client.Partner;
import org.kopi.ebics.client.User;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.Configuration;
import org.kopi.ebics.interfaces.EbicsBank;
import org.kopi.ebics.interfaces.EbicsUser;
import org.kopi.ebics.interfaces.PasswordCallback;
import org.kopi.ebics.io.IOUtils;
import org.kopi.ebics.messages.Messages;
import org.kopi.ebics.session.DefaultConfiguration;
import org.kopi.ebics.utils.Constants;

public class EbicsClient {

  public EbicsClient() {
    configuration = new DefaultConfiguration();
    users = new Hashtable<String, User>();
    partners = new Hashtable<String, Partner>();
    banks = new Hashtable<String, Bank>();
    Messages.setLocale(configuration.getLocale());
    
    this.init();
  }
  
  /**
   * Initiates the application by creating the
   * application root directories and its children
   */
  public void init() {
    configuration.getLogger().info(Messages.getString("init.configuration",
                                                      Constants.APPLICATION_BUNDLE_NAME));
    configuration.init();
  }
  
  /**
   * Creates a new EBICS bank with the data you should have obtained from the bank.
   * @param url the bank URL
   * @param url the bank name
   * @param hostId the bank host ID
   * @return the created ebics bank
   */
  public Bank createBank(URL url, String name, String hostId) {
    Bank                bank;
    
    bank = new Bank(url, name, hostId);
    banks.put(hostId, bank);
    return bank;
  }
  
  /**
   * Creates a new ebics partner
   * @param bank the bank
   * @param partnerId the partner ID
   */
  public Partner createPartner(EbicsBank bank, String partnerId) {
    Partner             partner;
    
    partner = new Partner(bank, partnerId);
    partners.put(partnerId, partner);
    return partner;
  }
  
  /**
   * Creates the user necessary directories
   * @param user the concerned user
   */
  public void createUserDirectories(EbicsUser user) {
    configuration.getLogger().info(Messages.getString("user.create.directories",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      user.getUserId()));
    //Create the user directory
    IOUtils.createDirectories(configuration.getUserDirectory(user));
    //Create the traces directory
    IOUtils.createDirectories(configuration.getTransferTraceDirectory(user));
    //Create the key stores directory
    IOUtils.createDirectories(configuration.getKeystoreDirectory(user));
    //Create the letters directory
    IOUtils.createDirectories(configuration.getLettersDirectory(user));
  }
  
  /**
   * Loads a user knowing its ID 
   * @param hostId the host ID
   * @param partnerId the partner ID
   * @param userId the user ID
   */
  public void loadUser(String hostId, 
                       String partnerId, 
                       String userId,
                       PasswordCallback passwordCallback) 
  { 
    configuration.getLogger().info(Messages.getString("user.load.info",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
    
    try {
      Bank                              bank;
      Partner                           partner;
      User                              user;
      ObjectInputStream                 input;
      
      input = configuration.getSerializationManager().deserialize(hostId);
      bank = (Bank)input.readObject();
      input = configuration.getSerializationManager().deserialize(partnerId);
      partner = new Partner(bank, input);
      input = configuration.getSerializationManager().deserialize(userId);
      user = new User(partner, input, passwordCallback);
      users.put(userId, user);
      partners.put(partner.getPartnerId(), partner);
      banks.put(bank.getHostId(), bank);
    } catch (EbicsException e) {
      configuration.getLogger().error(Messages.getString("user.load.error",
                                                         Constants.APPLICATION_BUNDLE_NAME),
                                      e);
      return;
    } catch (IOException e) {
      configuration.getLogger().error(Messages.getString("user.load.error",
                                                         Constants.APPLICATION_BUNDLE_NAME),
                                      e);
      return;
    } catch (ClassNotFoundException e) {
      configuration.getLogger().error(Messages.getString("user.load.error",
                                                         Constants.APPLICATION_BUNDLE_NAME),
                                      e);
      return;
    } catch (GeneralSecurityException e) {
      configuration.getLogger().error(Messages.getString("user.load.error",
                                                         Constants.APPLICATION_BUNDLE_NAME),
                                      e);
      return;
    }
    
    configuration.getLogger().info(Messages.getString("user.load.success",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
  }
  
  
  /**
   * Performs buffers save before quitting the client application.
   */
  public void quit() {
    try {
      for (User user : users.values()) {
        if (user.needsSave()) {
          configuration.getLogger().info(Messages.getString("app.quit.users",
                                                            Constants.APPLICATION_BUNDLE_NAME,
                                                            user.getUserId()));
          configuration.getSerializationManager().serialize(user);
        }
      }
      
      for (Partner partner : partners.values()) {
        if (partner.needsSave()) {
          configuration.getLogger().info(Messages.getString("app.quit.partners",
                                                            Constants.APPLICATION_BUNDLE_NAME,
                                                            partner.getPartnerId()));
          configuration.getSerializationManager().serialize(partner);
        }
      }
      
      for (Bank bank : banks.values()) {
        if (bank.needsSave()) {
          configuration.getLogger().info(Messages.getString("app.quit.banks",
                                                            Constants.APPLICATION_BUNDLE_NAME,
                                                            bank.getHostId()));
          configuration.getSerializationManager().serialize(bank);
        }
      }
    } catch (EbicsException e) {
      configuration.getLogger().info(Messages.getString("app.quit.error",
                                                        Constants.APPLICATION_BUNDLE_NAME));
    }
    
    configuration.getLogger().info(Messages.getString("app.cache.clear",
                                                      Constants.APPLICATION_BUNDLE_NAME));
    //configuration.getTraceManager().clear();
  }
  //--------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  
  protected Configuration               configuration;
  protected Map<String, User>           users;
  protected Map<String, Partner>        partners;
  protected Map<String, Bank>           banks;
  
  public static String URL_EBICS_SERVER = "https://server-ebics.webank.fr:28103/WbkPortalFileTransfert/EbicsProtocol";
  public static String BANK_NAME = "VALERIAN";
  
  static {
    org.apache.xml.security.Init.init();
    java.security.Security.addProvider(new BouncyCastleProvider());
  }
}
