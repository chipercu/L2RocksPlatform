package com.fuzzy.main.passwordencrypt;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.impl.ContextImpl;
import com.fuzzy.main.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.crypto.CryptoPassword;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.nio.file.Path;
import java.util.Scanner;

public class PasswordEncryptMain {
    public static void main(String[] args) {
        PasswordEncryptArgumentParser passwordEncryptArgumentParser;
        try {
            passwordEncryptArgumentParser = new PasswordEncryptArgumentParser(args);
        } catch (InterruptedException e) {
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.System.TYPE_CRUSH),
                    new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                    new ContextImpl(new SourceSystemImpl())
            );
            System.exit(2);
            return;
        }
        String password = passwordEncryptArgumentParser.password;
        String path = passwordEncryptArgumentParser.secret_key_path;
        try {
            CryptoPassword cryptoPassword = new CryptoPassword(Path.of(path));
            String encrypt = cryptoPassword.encrypt(password);
            System.out.println("Password: " + encrypt);
        } catch (PlatformException e) {
            System.out.println(e.getCode() + " " + e.getComment() + "\n");
            System.exit(2);
            return;
        }
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
