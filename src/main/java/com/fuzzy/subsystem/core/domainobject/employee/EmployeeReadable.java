package com.fuzzy.subsystem.core.domainobject.employee;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.infomaximum.database.anotation.PrefixIndex;
import com.infomaximum.database.utils.EnumConverter;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Optional;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "Employee",
        fields = {
                @Field(name = "department_id", number = EmployeeReadable.FIELD_DEPARTMENT_ID,
                        type = Long.class, foreignDependency = DepartmentReadable.class),
                @Field(name = "email", number = EmployeeReadable.FIELD_EMAIL,
                        type = String.class),
                @Field(name = "first_name", number = EmployeeReadable.FIELD_FIRST_NAME,
                        type = String.class),
                @Field(name = "patronymic", number = EmployeeReadable.FIELD_PATRONYMIC,
                        type = String.class),
                @Field(name = "second_name", number = EmployeeReadable.FIELD_SECOND_NAME,
                        type = String.class),
                @Field(name = "password_hash", number = EmployeeReadable.FIELD_PASSWORD_HASH,
                        type = byte[].class),
                @Field(name = "salt", number = EmployeeReadable.FIELD_SALT,
                        type = byte[].class),
                @Field(name = "language", number = EmployeeReadable.FIELD_LANGUAGE,
                        type = Language.class, packerType = EmployeeReadable.LanguagePacker.class),
                @Field(name = "personnel_number", number = EmployeeReadable.FIELD_PERSONNEL_NUMBER,
                        type = String.class),
                @Field(name = "login", number = EmployeeReadable.FIELD_LOGIN,
                        type = String.class),
                @Field(name = "need_to_change_password", number = EmployeeReadable.FIELD_NEED_TO_CHANGE_PASSWORD,
                        type = Boolean.class),
                @Field(name = "send_system_events", number = EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS,
                        type = Boolean.class)
        },
        hashIndexes = {
                @HashIndex(fields = { EmployeeReadable.FIELD_EMAIL }),
                @HashIndex(fields = { EmployeeReadable.FIELD_DEPARTMENT_ID }),
                @HashIndex(fields = { EmployeeReadable.FIELD_PERSONNEL_NUMBER }),
                @HashIndex(fields = { EmployeeReadable.FIELD_LOGIN }),
                @HashIndex(fields = { EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS })
        },
        prefixIndexes = {
                @PrefixIndex(fields = {
                        EmployeeReadable.FIELD_FIRST_NAME,
                        EmployeeReadable.FIELD_PATRONYMIC,
                        EmployeeReadable.FIELD_SECOND_NAME
                })
        }
)
public class EmployeeReadable extends RDomainObject {

    public final static int FIELD_DEPARTMENT_ID = 0;
    public final static int FIELD_EMAIL = 1;
    public final static int FIELD_FIRST_NAME = 2;
    public final static int FIELD_PATRONYMIC = 3;
    public final static int FIELD_SECOND_NAME = 4;
    public final static int FIELD_PASSWORD_HASH = 5;
    public final static int FIELD_SALT = 6;
    public final static int FIELD_LANGUAGE = 7;
    public final static int FIELD_PERSONNEL_NUMBER = 8;
    public final static int FIELD_LOGIN = 9;
    public final static int FIELD_NEED_TO_CHANGE_PASSWORD = 10;
    public final static int FIELD_SEND_SYSTEM_EVENTS = 11;

    public static class LanguagePacker extends EnumConverter<Language> {
        public LanguagePacker() {
            super(Language.class);
        }
    }

    public EmployeeReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public Long getDepartmentId() {
        return getLong(FIELD_DEPARTMENT_ID);
    }

    public String getEmail() {
        return getString(FIELD_EMAIL);
    }

    public String getLogin() {
        return getString(FIELD_LOGIN);
    }

    public String getFirstName() {
        return getString(FIELD_FIRST_NAME);
    }

    public String getPatronymic() {
        return getString(FIELD_PATRONYMIC);
    }

    public String getSecondName() {
        return getString(FIELD_SECOND_NAME);
    }

    public boolean hasPassword() {
        return get(FIELD_PASSWORD_HASH) != null;
    }

    public Language getLanguage() {
        return get(FIELD_LANGUAGE);
    }

    public byte[] getPasswordHash() {
        return get(FIELD_PASSWORD_HASH);
    }

    public byte[] getSalt() {
        return get(FIELD_SALT);
    }

    public boolean checkPasswordHash(String passwordHash) {
        byte[] saltyPasswordHash = getSaltyPasswordHash(passwordHash, getSalt());
        return saltyPasswordHash != null && Arrays.equals(saltyPasswordHash, this.getPasswordHash());
    }

    public String getPersonnelNumber() {
        return get(FIELD_PERSONNEL_NUMBER);
    }

    public static byte[] getSaltyPasswordHash(String passwordHash, byte[] salt) {
        if (passwordHash == null || salt == null) {
            return null;
        }
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec ks = new PBEKeySpec(passwordHash.toLowerCase().toCharArray(), salt, 20000, 512);
            return secretKeyFactory.generateSecret(ks).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDisplayName(DisplayNameFormat format) {
        Joiner joiner = Joiner.on(' ').skipNulls();
        String displayName = null;
        switch (format) {
            case FIRST_SECOND:
                displayName = joiner.join(
                        Strings.emptyToNull(getFirstName()),
                        Strings.emptyToNull(getPatronymic()),
                        Strings.emptyToNull(getSecondName())
                );
                break;
            case SECOND_FIRST:
                displayName = joiner.join(
                        Strings.emptyToNull(getSecondName()),
                        Strings.emptyToNull(getFirstName()),
                        Strings.emptyToNull(getPatronymic())
                );
                break;
        }
        return displayName;
    }

    public boolean isNeedToChangePassword() {
        return Optional.ofNullable(getBoolean(FIELD_NEED_TO_CHANGE_PASSWORD)).orElse(false);
    }

    public boolean isSendSystemEvents() {
        return Optional.ofNullable(getBoolean(FIELD_SEND_SYSTEM_EVENTS)).orElse(false);
    }
}
