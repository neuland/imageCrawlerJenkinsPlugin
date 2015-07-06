package de.neuland.imgc.command.impl;

import hudson.model.BuildListener;

public class CommandExecutorFactory {

    public CommandExecutor createCommandExecutor(final BuildListener listener, final String archiveDirSubPath) {
        return new CommandExecutor(listener, archiveDirSubPath);
    }

}
