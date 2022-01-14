/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and SmartUt
 * contributors
 *
 * This file is part of SmartUt.
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */
package org.smartut.intellij;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.smartut.intellij.util.AsyncGUINotifier;
import org.smartut.intellij.util.SmartUtExecutor;

/**
 * Created by arcuri on 10/15/14.
 */
public class StopSmartUtAction extends AnAction {

    private final AsyncGUINotifier notifier;

    public StopSmartUtAction(AsyncGUINotifier notifier){
        super("Stop SmartUt");
        getTemplatePresentation().setIcon(AllIcons.Actions.CloseNew);
        getTemplatePresentation().setHoveredIcon(AllIcons.Actions.CloseNewHovered);
        this.notifier = notifier;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SmartUtExecutor.getInstance().stopRun();
        //notifier.printOnConsole("\n\n\nSmartUt run has been cancelled\n"); //done in the Task
    }
}
