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

package com.bialx.ebics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bialx.ebics.options.BialxOptions;
import com.bialx.ebics.options.CreationOptions;
import com.bialx.ebics.options.DownloadOptions;
import org.apache.commons.cli.*;
import org.kopi.ebics.client.FileTransfer;
import org.kopi.ebics.client.User;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.PasswordCallback;
import org.kopi.ebics.messages.Messages;
import org.kopi.ebics.security.UserPasswordHandler;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.OrderType;
import org.kopi.ebics.session.Product;
import org.kopi.ebics.utils.Constants;

public class FDL extends Client {

    private static final String CERT_PASSWORD = "korian";

    public FDL() {
        super();
    }

    public void fetchFile(String output,
                          String userId,
                          String format,
                          Product product,
                          OrderType orderType,
                          boolean isTest,
                          Date start,
                          Date end)
    {
        FileTransfer                transferManager;
        EbicsSession                session;

        session = new EbicsSession(users.get(userId), configuration);
        session.addSessionParam("FORMAT", format);
        if (isTest) {
            session.addSessionParam("TEST", "true");
        }
        session.setProduct(product);
        transferManager = new FileTransfer(session);

        configuration.getTraceManager().setTraceDirectory(configuration.getTransferTraceDirectory(users.get(userId)));

        try {
            transferManager.fetchFile(orderType, start, end, new FileOutputStream(configuration.getDownloadsDirectory() + File.separator + output));
        } catch (IOException e) {
            configuration.getLogger().error(Messages.getString("download.file.error",
                            Constants.APPLICATION_BUNDLE_NAME),
                    e);
        } catch (EbicsException e) {
            configuration.getLogger().error(Messages.getString("download.file.error",
                            Constants.APPLICATION_BUNDLE_NAME),
                    e);
        }
    }

    public static void main(String[] args) throws Exception {
        String      userId = "";
        Boolean     isTest = false;
        Date        startDate = null;
        Date        endDate = null;

        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");

        CommandLineParser parser = new DefaultParser();

        BialxOptions options = new BialxOptions();

        // Parse the program arguments
        CommandLine commandLine = parser.parse(options, args);

        // optional values
        if (commandLine.hasOption('s')){
            startDate = dtFormat.parse(commandLine.getOptionValue('s'));
        }

        if (commandLine.hasOption('e')){
            endDate = dtFormat.parse(commandLine.getOptionValue('e'));
        }

        if (commandLine.hasOption('t')){
            isTest = true;
        }

        FDL fdl;
        PasswordCallback    pwdHandler;
        Product             product;

        fdl = new FDL();

        pwdHandler = new UserPasswordHandler(userId, CERT_PASSWORD);

        product = new Product("Bial-x EBICS FDL", Locale.FRANCE, null);

        if(commandLine.hasOption(BialxOptions.OPTION_CREATION) && !commandLine.hasOption(BialxOptions.OPTION_DOWNLOAD)){
            if(options.checkCreationOptions(commandLine)){
                CreationOptions co = options.loadCreationOptions(commandLine);
                fdl.configuration.getLogger().info(String.format("Banque : %s",co.getBankName()));
                fdl.configuration.getLogger().info(String.format("Host : %s",co.getHostId()));
                fdl.configuration.getLogger().info(String.format("URL : %s",co.getBankUrl()));
                fdl.configuration.getLogger().info(String.format("Partner : %s",co.getPartnerId()));
                fdl.configuration.getLogger().info(String.format("User : %s",co.getUserId()));
                User user = fdl.createUser(co.getUserId(),co.getHostId(),co.getPartnerId(),co.getBankName(),co.getBankUrl(),pwdHandler);
                fdl.sendHPBRequest(user,product);
            }else{
                fdl.configuration.getLogger().info("Vérifiez les paramètres de la commande.");
                System.exit(0);
            }
        }

        if(!commandLine.hasOption(BialxOptions.OPTION_CREATION) && commandLine.hasOption(BialxOptions.OPTION_DOWNLOAD)){
            if(options.checkDownloadOptions(commandLine)){
                DownloadOptions dop= options.loadDownloadOptions(commandLine);
                fdl.configuration.getLogger().info(String.format("Banque : %s",dop.getHostId()));
                fdl.configuration.getLogger().info(String.format("Host : %s",dop.getHostId()));
                fdl.configuration.getLogger().info(String.format("Partner : %s",dop.getPartnerId()));
                fdl.configuration.getLogger().info(String.format("User : %s",dop.getUserId()));
                fdl.configuration.getLogger().info(String.format("Format : %s",dop.getFormat()));
                fdl.configuration.getLogger().info(String.format("Destination : %s",dop.getDestination()));
                if(startDate != null) {
                    fdl.configuration.getLogger().info(String.format("Date début : %s", dtFormat.format(startDate)));
                }
                if(endDate != null) {
                    fdl.configuration.getLogger().info(String.format("Date fin : %s", dtFormat.format(endDate)));
                }
                fdl.loadUser(dop.getHostId(),dop.getPartnerId(),dop.getUserId(),pwdHandler);
                fdl.fetchFile(dop.getDestination(), dop.getUserId(), dop.getFormat(), product, OrderType.FDL, isTest, startDate, endDate);
            }else{
                fdl.configuration.getLogger().info("Vérifiez les paramètres de la commande.");
                System.exit(0);
            }
        }

        if(commandLine.hasOption(BialxOptions.OPTION_DOWNLOAD) && commandLine.hasOption(BialxOptions.OPTION_CREATION)){
            fdl.configuration.getLogger().error("Impossible d'avoir les options C et D dans la même commande.");
            System.exit(0);
        }

        fdl.quit();
    }

}
