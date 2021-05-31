package com.github.xuchengen.leo.plug.action;

import com.github.xuchengen.leo.plug.LeoBundle;
import com.github.xuchengen.leo.plug.service.PrintService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 停止动作<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/26 2:35 下午<br>
 */
public class StopAction extends AnAction {

    public StopAction() {
        super(LeoBundle.message("action.stop"), "", AllIcons.Actions.Suspend);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PrintService printService = e.getProject().getService(PrintService.class);
        if (printService.isRun()) {
            printService.stop();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PrintService printService = e.getProject().getService(PrintService.class);
        if (printService.isRun()) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
