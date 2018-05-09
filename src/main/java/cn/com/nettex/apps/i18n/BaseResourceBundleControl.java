package cn.com.nettex.apps.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * <p>Title: science</p>
 * <p>Description: BaseResourceBundleControl</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 05/09/2018
 */
public class BaseResourceBundleControl extends ResourceBundle.Control {

    public BaseResourceBundleControl() {
    }

    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
        throws IllegalAccessException, InstantiationException, IOException {
        String bundleName = toBundleName(baseName, locale);
        ResourceBundle bundle = null;
        switch (format) {
            case "java.class":
                bundle = super.newBundle(baseName, locale, format, loader, reload);
                break;
            case "java.properties":
                final String resourceName = bundleName.contains("://") ? null :
                    toResourceName(bundleName, "properties");
                if (resourceName == null) {
                    return null;
                }
                InputStream stream;
                if (reload) {
                    stream = reload(resourceName, loader);
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }
                if (stream != null) {
                    try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                        bundle = new PropertyResourceBundle(reader);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
        return bundle;
    }

    private InputStream reload(String resourceName, ClassLoader classLoader) throws IOException {
        InputStream stream = null;
        URL url = classLoader.getResource(resourceName);
        if (url != null) {
            URLConnection connection = url.openConnection();
            if (connection != null) {
                connection.setUseCaches(false);
                stream = connection.getInputStream();
            }
        }
        return stream;
    }

}
