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

import java.util.Locale;

import com.bialx.ebics.Client;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.kopi.ebics.client.KeyManagement;
import org.kopi.ebics.client.User;
import org.kopi.ebics.interfaces.PasswordCallback;
import org.kopi.ebics.messages.Messages;
import org.kopi.ebics.security.UserPasswordHandler;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.Product;
import org.kopi.ebics.utils.Constants;

public class UserIntiator extends Client {
  
  public UserIntiator() {
    super();
  }
  
  /**
   * Sends an INI request to the ebics bank server
   * @param userId the user ID
   * @param product the application product
   */
  public void sendINIRequest(String userId, Product product) {
    User                        user;
    EbicsSession                session;
    KeyManagement               keyManager;
    
    configuration.getLogger().info(Messages.getString("ini.request.send",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
    
    user = users.get(userId);
    
    if (user.isInitialized()) {
      configuration.getLogger().info(Messages.getString("user.already.initialized",
                                                        Constants.APPLICATION_BUNDLE_NAME,
                                                        userId));
      return;
    }
    
    session = new EbicsSession(user, configuration);
    session.setProduct(product);
    keyManager = new KeyManagement(session);
    configuration.getTraceManager().setTraceDirectory(configuration.getTransferTraceDirectory(user));
    
    try {
      keyManager.sendINI(null);
    } catch (Exception e) {
      configuration.getLogger().error(Messages.getString("ini.send.error",
                                                         Constants.APPLICATION_BUNDLE_NAME,
                                                         userId),
                                      e);
      return;
    }
    
    user.setInitialized(true);
    configuration.getLogger().info(Messages.getString("ini.send.success",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
  }
  
  /**
   * Sends a HIA request to the ebics server.
   * @param userId the user ID.
   * @param product the application product.
   */
  public void sendHIARequest(String userId, Product product) {
    User                        user;
    EbicsSession                session;
    KeyManagement               keyManager;
    
    configuration.getLogger().info(Messages.getString("hia.request.send",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
    user = users.get(userId);
    if (user.isInitializedHIA()) {
      configuration.getLogger().info(Messages.getString("user.already.hia.initialized",
                                                        Constants.APPLICATION_BUNDLE_NAME,
                                                        userId));
      return;
    }
    session = new EbicsSession(user, configuration);
    session.setProduct(product);
    keyManager = new KeyManagement(session);
    configuration.getTraceManager().setTraceDirectory(configuration.getTransferTraceDirectory(user));
    
    try {
      keyManager.sendHIA(null);
    } catch (Exception e) {
      configuration.getLogger().error(Messages.getString("hia.send.error",
                                                         Constants.APPLICATION_BUNDLE_NAME,
                                                         userId),
                                      e);
      return;
    }
    
    user.setInitializedHIA(true);
    configuration.getLogger().info(Messages.getString("hia.send.success",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
  }
  
  public static void main(String[] args) throws Exception {
    String      hostId = "";
    String      partnerId = "";
    String      userId = "";
    
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "host", true, "EBICS Host ID");
    options.addOption("p", "partner", true, "Registred Partner ID for you user");
    options.addOption("u", "user", true, "User ID to initiate" );
    
    // Parse the program arguments
    CommandLine commandLine = parser.parse(options, args);
    
    if (!commandLine.hasOption('h')) {
      System.out.println("Host-ID is mandatory");
      System.exit(0);
    } else {
      hostId = commandLine.getOptionValue('h');
      System.out.println("host: " + hostId);
    }
    
    if (!commandLine.hasOption('p')) {
      System.out.println("Partner-ID is mandatory");
      System.exit(0);
    } else {
      partnerId = commandLine.getOptionValue('p');
      System.out.println("partnerId: " + partnerId);
    }
    
    if (!commandLine.hasOption('u')) {
      System.out.println("User-ID is mandatory");
      System.exit(0);
    } else {
      userId = commandLine.getOptionValue('u');
      System.out.println("userId: " + userId);
    }
    
    UserIntiator        userIntiator;
    PasswordCallback    pwdHandler;
    Product             product;
    
    userIntiator = new UserIntiator();
    product = new Product("kopiLeft Dev 1.0", Locale.FRANCE, null);
    pwdHandler = new UserPasswordHandler(userId, "2012");
    
    // Load alredy created user
    userIntiator.loadUser(hostId, partnerId, userId, pwdHandler);
    
    // INIT Request
    userIntiator.sendINIRequest(userId, product);
    // HIA Request
    userIntiator.sendHIARequest(userId, product);
    
  }

}
