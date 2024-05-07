package com.example.shoppapp.Components;

import com.example.shoppapp.Services.CategoryService;
import com.example.shoppapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalLizationUtils {
    private final MessageSource messageSource;
    // tải các thông điệp (messages) được định nghĩa trong các tệp tin cấu hình (properties files).
    private final LocaleResolver localeResolver;
    // được sử dụng để xác định locale (vùng miền) của người dùng từ yêu cầu (request).
    // LocaleResolver thường được sử dụng trong việc cấu hình phần i18n/l10n của ứng dụng web Spring.
//Object...params :spread operator
    public String getLocalzedMessage(String messageKey,Object... params){
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale= localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,params,locale);
    }
}
