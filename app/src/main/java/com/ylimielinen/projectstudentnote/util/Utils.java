package com.ylimielinen.projectstudentnote.util;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by kb on 16.11.2017.
 */

public class Utils {

    public static boolean updateLanguage(Context baseContext, String lang){
        // Update of the language
        if (!lang.isEmpty() && baseContext != null) {
            // Create a new locale and set it as default
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            baseContext.getResources().updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
            return true;
        }
        return false;

    }
}
