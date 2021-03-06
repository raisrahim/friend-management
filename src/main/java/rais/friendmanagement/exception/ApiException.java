package rais.friendmanagement.exception;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Muhammad Rais Rahim <rais.gowa@gmail.com>
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@Getter
@Slf4j
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -2193200205752285811L;

    private final String errorCode;
    private final Object[] messageArgs;

    public ApiException(String errorCode) {
        this(errorCode, null, null);
    }

    public ApiException(String errorCode, Object[] messageArgs) {
        this(errorCode, null, messageArgs);
    }

    public ApiException(String errorCode, Throwable cause) {
        this(errorCode, cause, null);
    }

    public ApiException(String errorCode, Throwable cause, Object[] messageArgs) {
        super(cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    @Override
    public String getMessage() {
        return getLocalizedMessage(null);
    }

    public String getLocalizedMessage(Locale locale) {
        try {
            String msg = getResourceBundle(locale).getString(getErrorCode());
            if (messageArgs != null) {
                msg = MessageFormat.format(msg, messageArgs);
            }
            return msg;
        } catch (MissingResourceException mre) {
            log.warn("[getLocalizedMessage]-returning the class name", mre);
            return getClass().getSimpleName();
        }
    }

    public static ResourceBundle getResourceBundle() {
        return getResourceBundle(null);
    }

    public static ResourceBundle getResourceBundle(Locale loc) {
        Locale locale = (loc == null) ? Locale.getDefault() : loc;
        ResourceBundle rb = resourceBundleMap.get(locale);
        if (rb == null) {
            rb = ResourceBundle.getBundle("messages.AppException", locale);
            resourceBundleMap.put(locale, rb);
        }
        log.debug("[getResourceBundle]-paramLocale={}, found locale={}", loc, rb.getLocale());
        return rb;
    }
    private static final Map<Locale, ResourceBundle> resourceBundleMap = new HashMap<>();

    public static boolean isSupportedLocale(Locale locale) {
        try {
            getResourceBundle(locale);
            return true;
        } catch (MissingResourceException mre) {
            return false;
        }
    }
}
