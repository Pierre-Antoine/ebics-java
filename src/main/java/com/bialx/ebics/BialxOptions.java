package com.bialx.ebics;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Created by pierre-antoine.marc on 15/07/2016.
 */
public class BialxOptions extends Options{
    public BialxOptions() {
        super();
        this.addOption("h", "host", true, "EBICS Host ID");
        this.addOption("p", "partner", true, "Registred Partner ID for your user");
        this.addOption("u", "user", true, "User ID to initiate" );
        this.addOption("f", "format", true, "Format type to request" );
        this.addOption("o", "output", true, "Output file location" );
        this.addOption("b", "bankurl", true, "Bank Ebics URL" );
        this.addOption("B","bankname", true, "Name of the bank");
        this.addOption("s", "start", true, "Start date" );
        this.addOption("e", "end", true, "End date" );
        this.addOption("t", "test", false, "Test request" );
        this.addOption("C","createUser",false,"Creates a user");
    }

    public Boolean checkCreationOptions(CommandLine commandLine){
        if(!commandLine.hasOption("h") || !commandLine.hasOption("p") || !commandLine.hasOption("u") || !commandLine.hasOption("b") || !commandLine.hasOption("B")){
           return false;
        }
        return true;
    }

    public CreationOptions loadCreationOptions(CommandLine commandLine){
        return new CreationOptions(commandLine.getOptionValue("u"),
                                    commandLine.getOptionValue("h"),
                                    commandLine.getOptionValue("b"),
                                    commandLine.getOptionValue("B"),
                                    commandLine.getOptionValue("p"));
    }

}
