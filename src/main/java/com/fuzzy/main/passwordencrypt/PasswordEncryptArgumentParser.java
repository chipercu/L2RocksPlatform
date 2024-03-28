package com.fuzzy.main.passwordencrypt;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PasswordEncryptArgumentParser {
    private static final String OPTION_PASSWORD = "password";
    private static final String OPTION_SECRET_KEY_PATH = "secret_key_path";

    public String password;
    public String secret_key_path;

    public PasswordEncryptArgumentParser(String[] args) throws InterruptedException {
        Options options = new Options()
                .addOption(Option.builder()
                        .longOpt(OPTION_PASSWORD)
                        .hasArg(true)
                        .required()
                        .desc("Password")
                        .build())
                .addOption(Option.builder()
                        .longOpt(OPTION_SECRET_KEY_PATH)
                        .hasArg(true)
                        .required()
                        .desc("Path to the secret_key")
                        .build());

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            password = cmd.getOptionValue(OPTION_PASSWORD);
            secret_key_path = cmd.getOptionValue(OPTION_SECRET_KEY_PATH);
            Path path = Paths.get(secret_key_path);
            if (path.getNameCount() == 0 || !path.toFile().isFile()) {
                throw new IllegalArgumentException("invalid path to secret key");
            }

        } catch (ParseException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            new HelpFormatter().printHelp("app_name", options);

            throw new InterruptedException();
        }
    }

}
