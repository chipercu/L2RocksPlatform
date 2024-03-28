package com.fuzzy.subsystems.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: kris
 * Date: 05.04.12
 * Time: 13:54
 * Класс отвечает за локализацию
 */
public class MessageResourceSource {

//    public static final ResourceBundle.Control RESOURCE_CONTROL_UTF8 = new ResourceControlUTF8();

    public static ResourceBundle get(String baseName, Locale locale) {
        try {
//            return ResourceBundle.getBundle("i18n/" + baseName + "_" + locale.getLanguage(), locale, RESOURCE_CONTROL_UTF8);
            return ResourceBundle.getBundle("cis-i18n/" + baseName.replace('.', '-') + "_" + locale.getLanguage(), locale);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /*
    public static class ResourceControlUTF8 extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;

            //Изменили подход - т.к. реализация по умолчанию все точки заменяет на косую черту
            final String resourceName = bundleName + ".properties";
//            final String resourceName = toResourceName(bundleName, "properties"); // <- change this line
//            if (resourceName == null) {
//                return bundle;
//            }

            final ClassLoader classLoader = loader;
            final boolean reloadFlag = reload;
            InputStream stream = null;
            try {
                stream = AccessController.doPrivileged(
                        new PrivilegedExceptionAction<InputStream>() {
                            public InputStream run() throws IOException {
                                InputStream is = null;
                                if (reloadFlag) {
                                    URL url = classLoader.getResource(resourceName);
                                    if (url != null) {
                                        URLConnection connection = url.openConnection();
                                        if (connection != null) {
                                            // Disable caches to get fresh data for
                                            // reloading.
                                            connection.setUseCaches(false);
                                            is = connection.getInputStream();
                                        }
                                    }
                                } else {
                                    is = classLoader.getResourceAsStream(resourceName);
                                }
                                return is;
                            }
                        });
            } catch (PrivilegedActionException e) {
                throw (IOException) e.getException();
            }
            if (stream != null) {
                try (InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) { // <- change this line
                    bundle = new PropertyResourceBundle(streamReader); // <- change this line
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
     */
}
