package com.github.xuchengen.leo.plug.action;

import com.github.xuchengen.leo.plug.LeoBundle;
import com.github.xuchengen.leo.plug.service.PrintService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 启动动作<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/26 4:32 下午<br>
 */
public class StartupAction extends AnAction {

    public StartupAction() {
        super(LeoBundle.message("action.start"), "", AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PrintService printService = e.getProject().getService(PrintService.class);
        if (!printService.isRun()) {
            printService.start();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PrintService printService = e.getProject().getService(PrintService.class);
        if (printService.isRun()) {
            e.getPresentation().setEnabledAndVisible(false);
        } else {
            e.getPresentation().setEnabledAndVisible(true);
        }
    }
}
