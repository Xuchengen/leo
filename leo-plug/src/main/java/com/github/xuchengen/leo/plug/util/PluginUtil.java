package com.github.xuchengen.leo.plug.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.leo.plug.Constant;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

import java.io.File;
import java.util.List;

/**
 * 插件工具类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/5/7 1:25 下午<br>
 */
public class PluginUtil {

    private static final IdeaPluginDescriptor IDEA_PLUGIN_DESCRIPTOR;

    static {
        PluginId pluginId = PluginId.getId(Constant.PLUGIN_ID);
        IDEA_PLUGIN_DESCRIPTOR = PluginManagerCore.getPlugin(pluginId);
    }

    /**
     * 获取插件路径
     *
     * @return String
     */
    public static String getPluginPath() {
        return IDEA_PLUGIN_DESCRIPTOR.getPluginPath().toString();
    }

    /**
     * 获取核心Jar路径
     *
     * @return String
     */
    public static String getAgentCoreJarPath() {
        return getJarPathByStartWith(Constant.AGENT_CORE_JAR_NAME);
    }

    /**
     * 根据jar包的前缀名称获路径
     *
     * @param startWith 前缀名称
     * @return String
     */
    private static String getJarPathByStartWith(String startWith) {
        final String quotes = "\"";
        List<File> files = FileUtil.loopFiles(IDEA_PLUGIN_DESCRIPTOR.getPluginPath().toFile());
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(startWith)) {
                String pathStr = FileUtil.getCanonicalPath(file);
                if (StrUtil.contains(pathStr, StrUtil.SPACE)) {
                    return StrUtil.builder().append(quotes).append(pathStr).append(quotes).toString();
                }
                return pathStr;
            }
        }
        return StrUtil.EMPTY;
    }
}
