package com.github.xuchengen.leo.plug;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.leo.plug.util.PluginUtil;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;

import java.util.Objects;

/**
 * 运行前检查<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/5/6 3:20 下午<br>
 */
public class PerRunCheck extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (!(configuration instanceof RunConfiguration)) {
            return;
        }

        Sdk jdk = javaParameters.getJdk();

        if (Objects.isNull(jdk)) {
            return;
        }

        JavaSdkVersion version = JavaSdk.getInstance().getVersion(jdk);

        if (Objects.isNull(version)) {
            return;
        }

        if (version.compareTo(JavaSdkVersion.JDK_1_8) < 0) {
            return;
        }

        String agentCoreJarPath = PluginUtil.getAgentCoreJarPath();

        if (StrUtil.isBlank(agentCoreJarPath)) {
            return;
        }

        RunConfiguration runConfiguration = (RunConfiguration) configuration;
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        vmParametersList.addParametersString(Constant.AGENT_PREFIX + agentCoreJarPath);
        vmParametersList.addNotEmptyProperty(Constant.AGENT_PARAM_PROJECT_ID, runConfiguration.getProject().getLocationHash());
    }

}
