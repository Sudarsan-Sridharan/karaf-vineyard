package org.apache.karaf.vineyard.registry.api.command.completer;

import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.apache.karaf.vineyard.registry.api.ApiRegistryService;

import java.util.List;

@Service
public class ApiIdCompleter implements Completer {

    @Reference
    private ApiRegistryService apiRegistryService;

    @Override
    public int complete(Session session, CommandLine commandLine, List<String> list) {
        StringsCompleter delegate = new StringsCompleter();
        apiRegistryService.list().stream().map(api -> delegate.getStrings().add(api.getId()));
        return delegate.complete(session, commandLine, list);
    }

}
