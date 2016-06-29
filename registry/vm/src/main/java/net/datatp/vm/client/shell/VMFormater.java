package net.datatp.vm.client.shell;

import java.util.List;

import net.datatp.util.text.StringUtil;
import net.datatp.util.text.TabularFormater;
import net.datatp.vm.VMDescriptor;

public class VMFormater {
  static public String format(String title, List<VMDescriptor> descriptors) throws Exception {
    TabularFormater formater = new TabularFormater("ID", "Path", "Roles", "Cores", "Memory");
    formater.setIndent("  ");
    for(int i = 0; i < descriptors.size(); i++) {
      VMDescriptor descriptor = descriptors.get(i) ;
      formater.addRow(
          descriptor.getVmId(), 
          descriptor.getRegistryPath(),
          StringUtil.join(descriptor.getVmConfig().getRoles(), ","),
          descriptor.getCpuCores(),
          descriptor.getMemory()
      );
    }
    formater.setTitle(title);
    return formater.getFormatText();
  }
}
