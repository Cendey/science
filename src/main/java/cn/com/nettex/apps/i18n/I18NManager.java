package cn.com.nettex.apps.i18n;

import cn.com.nettex.apps.meta.ConfigMeta;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

//https://www.sothawo.com/2016/09/how-to-implement-a-javafx-ui-where-the-language-can-be-changed-dynamically/
//https://stackoverflow.com/questions/21171249/how-to-reload-the-screen-when-changing-languages-in-javafx
//https://stackoverflow.com/questions/32464974/javafx-change-application-language-on-the-run
public class I18NManager {

    private final static Logger logger = LogManager.getLogger(I18NManager.class);

    /**
     * the current selected Locale.
     */
    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    /**
     * get the supported Locales.
     *
     * @return List of Locale objects.
     */
    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(
                Arrays.asList(Locale.ENGLISH, Locale.GERMAN, Locale.GERMANY, Locale.US, Locale.CHINESE,
                        Locale.SIMPLIFIED_CHINESE,
                        Locale.TRADITIONAL_CHINESE,
                        Locale.JAPAN, Locale.JAPANESE, Locale.KOREA, Locale.KOREAN));
    }

    /**
     * get the default locale. This is the systems default if contained in the supported locales, english otherwise.
     *
     * @return
     */
    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     *
     * @param key  message key
     * @param args optional arguments for the message
     * @return localized formatted string
     */
    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(ConfigMeta.MESSAGES_MESSAGE, getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

    /**
     * creates a String binding to a localized String for the given message bundle key
     *
     * @param key key
     * @return String binding
     */
    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    /**
     * creates a String Binding to a localized String that is computed by calling the given func
     *
     * @param func function called on every change
     * @return StringBinding
     */
    public static StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, locale);
    }

    /**
     * creates a bound Label whose value is computed on language change.
     *
     * @param func the function to compute the value
     * @return Label
     */
    public static Label labelForValue(Callable<String> func) {
        Label label = new Label();
        label.textProperty().bind(createStringBinding(func));
        return label;
    }

    /**
     * creates a bound Button for the given resource bundle key
     *
     * @param key  ResourceBundle key
     * @param args optional arguments for the message
     * @return Button
     */
    public static Button buttonForKey(final String key, final Object... args) {
        Button button = new Button();
        button.textProperty().bind(createStringBinding(key, args));
        return button;
    }

    /**
     * creates a bound Tooltip for the given resource bundle key
     *
     * @param key  ResourceBundle key
     * @param args optional arguments for the message
     * @return Label
     */
    public static Tooltip tooltipForKey(final String key, final Object... args) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(createStringBinding(key, args));
        return tooltip;
    }

    /**
     * creates a bound MenuItem for the given resource bundle key
     *
     * @param key  ResourceBundle key
     * @param args optional arguments for the message
     * @return MenuItem
     */
    public static MenuItem menuItemForKey(final String key, final Object... args) {
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(createStringBinding(key, args));
        return menuItem;
    }

    /**
     * creates a bound Menu for the given resource bundle key
     *
     * @param key  ResourceBundle key
     * @param args optional arguments for the message
     * @return Menu
     */
    public static Menu menuForKey(final String key, final Object... args) {
        Menu menu = new Menu();
        menu.textProperty().bind(createStringBinding(key, args));
        return menu;
    }

    /**
     * generally bind a key to control component for i18n.
     *
     * @param node a control component
     * @param key  resource bundle key
     * @param args optional arguments for the message
     */
    public static void bindKey(Object node, final String key, final Object... args) {
        Class<?> clazz = node.getClass();
        try {
            Method reader = clazz.getMethod("textProperty");
            Object property = reader.invoke(node);
            Class<?> chain = property.getClass();
            Method bind = chain.getMethod("bind", StringBinding.class);
            bind.invoke(property, createStringBinding(key, args));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getCause().getMessage());
        }
    }
}
