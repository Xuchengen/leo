package com.github.xuchengen.leo.plug;

import com.intellij.AbstractBundle;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * 国际化<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/5/11 2:24 下午<br>
 */
public class LeoBundle {

    private static Reference<ResourceBundle> ourBundle;

    private static final String BUNDLE = "i18n.i18n";

    private LeoBundle() {
    }

    public static String message(final String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (ourBundle != null) {
            bundle = ourBundle.get();
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }
}
