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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import com.bialx.ebics.Client;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.kopi.ebics.client.Bank;
import org.kopi.ebics.client.Partner;
import org.kopi.ebics.client.User;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.InitLetter;
import org.kopi.ebics.interfaces.PasswordCallback;
import org.kopi.ebics.messages.Messages;
import org.kopi.ebics.security.UserPasswordHandler;
import org.kopi.ebics.utils.Constants;


public class UserCreator extends Client {
  
  public UserCreator() {
    super();
  }
  
  /**
   * Creates a new ebics user and generates its certificates.
   * 
   * @param url the bank url
   * @param bankName the bank name
   * @param hostId the bank host ID
   * @param partnerId the partner ID
   * @param userId UserId as obtained from the bank.
   * @param name the user name,
   * @param email the user email
   * @param country the user country
   * @param organisation the user organization or company
   * @param saveCetificates save generated certificates?
   * @param passwordCallback a callback-handler that supplies us with the password. 
   *                         This parameter can be null, in this case no password is used.
   * @param saveCertificates
   */
  public void createUser(URL url,
                         String bankName,
                         String hostId,
                         String partnerId,
                         String userId,
                         String name,
                         String email,
                         String country,
                         String organization,
                         boolean saveCetificates,
                         PasswordCallback passwordCallback)
  {
    Bank                        bank;
    Partner                     partner;
    User                        user;
    InitLetter                  a005Letter;
    InitLetter                  x002Letter;
    InitLetter                  e002Letter;
    
    configuration.getLogger().info(Messages.getString("user.create.info",
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
    
    bank = createBank(url, bankName, hostId);
    partner = createPartner(bank, partnerId);
    try {
      user = new User(partner,
                      userId, 
                      name, 
                      email, 
                      country, 
                      organization, 
                      passwordCallback);
      
      createUserDirectories(user);
      if (saveCetificates) {
        user.saveUserCertificates(configuration.getKeystoreDirectory(user));
      }
      configuration.getSerializationManager().serialize(bank);
      configuration.getSerializationManager().serialize(partner);
      configuration.getSerializationManager().serialize(user);
      a005Letter = configuration.getLetterManager().createA005Letter(user);
      e002Letter = configuration.getLetterManager().createE002Letter(user);
      x002Letter = configuration.getLetterManager().createX002Letter(user);
      a005Letter.save(new FileOutputStream(configuration.getLettersDirectory(user) +
                                           File.separator + 
                                           a005Letter.getName()));
      e002Letter.save(new FileOutputStream(configuration.getLettersDirectory(user) +
                                           File.separator +
                                           e002Letter.getName()));
      x002Letter.save(new FileOutputStream(configuration.getLettersDirectory(user) +
                                           File.separator +
                                           x002Letter.getName()));
      users.put(userId, user);
      partners.put(partner.getPartnerId(), partner);
      banks.put(bank.getHostId(), bank);
    } catch (GeneralSecurityException e) {
      configuration.getLogger().error(Messages.getString("user.create.error",
                                                         Constants.APPLICATION_BUNDLE_NAME
                                                        ),
                                      e);
      return;
    } catch (IOException e) {
      configuration.getLogger().error(Messages.getString("user.create.error",
                                                         Constants.APPLICATION_BUNDLE_NAME
                                                         ),
                                      e);
      return;
    } catch (EbicsException e) {
      configuration.getLogger().error(Messages.getString("user.create.error",
                                                         Constants.APPLICATION_BUNDLE_NAME
                                                         ),
                                      e);
      return;
    }
    
    configuration.getLogger().info(Messages.getString("user.create.success", 
                                                      Constants.APPLICATION_BUNDLE_NAME,
                                                      userId));
  }

    public User getUser(String userId){
        return users.get(userId);
    }

  //--------------------------------------------------------------------
  // JVM ENTRY POINT
  // --------------------------------------------------------------------

  /**
   * JVM program entry point
   * @param args program arguments
   */
  public static void main(String[] args) throws Exception {
    
    String      hostId = "";
    String      partnerId = "";
    String      userId = "";
    
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("h", "host", true, "EBICS Host ID");
    options.addOption("p", "partner", true, "Registred Partner ID under which the user will be created");
    options.addOption("u", "user", true, "User ID to create" );
    
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
    
    UserCreator         userCreator;
    PasswordCallback    pwdHandler;
    
    userCreator = new UserCreator();
    pwdHandler = new UserPasswordHandler(userId, "2012");
    
    userCreator.createUser(new URL(Client.URL_EBICS_SERVER),
                           Client.BANK_NAME,
                           hostId,
                           partnerId,
                           userId,
                           "pebics",
                           "pebics@domaine.fr",
                           "France",
                           "Euro-Information",
                           true,
                           pwdHandler);
    
  }
  
}
