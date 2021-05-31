package com.github.xuchengen.leo.plug;

import cn.hutool.core.io.IoUtil;
import com.github.xuchengen.leo.plug.action.StartupAction;
import com.github.xuchengen.leo.plug.action.StopAction;
import com.github.xuchengen.leo.plug.service.PrintService;
import com.intellij.codeEditor.printing.PrintAction;
import com.intellij.execution.actions.ClearConsoleAction;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.actionSystem.ActionPlaces.UNKNOWN;

/**
 * 控制台窗口实现类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/25 1:05 下午<br>
 */
public class ConsoleToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 控制台视图
        ConsoleView consoleView = project.getService(PrintService.class).getConsoleView();
        // 输出控制台文本LOGO
        printLogo(consoleView);

        // 创建Action组
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        // 创建工具栏
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(UNKNOWN, actionGroup, false);
        actionToolbar.setTargetComponent(consoleView.getComponent());

        // 创建打印任务
        actionGroup.add(new StartupAction());
        actionGroup.add(new StopAction());
        actionGroup.addAll(getDefaultAction(consoleView));

        // 创建面板
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(consoleView.getComponent(), BorderLayout.CENTER);
        jPanel.add(actionToolbar.getComponent(), BorderLayout.WEST);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jPanel, StringUtils.EMPTY, false);
        toolWindow.getContentManager().addContent(content);
        Disposer.register(project, consoleView);
    }

    /**
     * 获取ConsoleView默认的几个Action
     *
     * @param consoleView 控制台视图
     * @return AnAction[]
     */
    AnAction[] getDefaultAction(ConsoleView consoleView) {
        AnAction[] consoleActions = consoleView.createConsoleActions();
        List<AnAction> myActions = new ArrayList<>();
        for (AnAction consoleAction : consoleActions) {
            if (consoleAction instanceof ToggleUseSoftWrapsToolbarAction) {
                myActions.add(consoleAction);
                continue;
            }

            if (consoleAction instanceof ScrollToTheEndToolbarAction) {
                myActions.add(consoleAction);
                continue;
            }

            if (consoleAction instanceof ClearConsoleAction) {
                myActions.add(consoleAction);
                continue;
            }

            if (consoleAction instanceof PrintAction) {
                myActions.add(consoleAction);
            }
        }
        return myActions.toArray(AnAction.EMPTY_ARRAY);
    }

    /**
     * 输出文本型LOGO
     *
     * @param consoleView 控制台视图
     */
    void printLogo(ConsoleView consoleView) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("logo.txt");
        if (Objects.nonNull(inputStream)) {
            String logoText = IoUtil.readUtf8(inputStream);
            consoleView.print(logoText, ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
    }
}
