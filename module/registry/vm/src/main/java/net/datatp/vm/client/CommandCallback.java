package net.datatp.vm.client;

import net.datatp.vm.command.Command;
import net.datatp.vm.command.CommandResult;

public interface CommandCallback {
  public void onResponse(Command command, CommandResult<?> result);
}
