package com.gmo.rmiconfig;

import java.io.FilePermission;
import java.net.SocketPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.PropertyPermission;

public class SecurityPolicy extends Policy {
	private static PermissionCollection perms;

    public SecurityPolicy() {
        super();
        if (perms == null) {
            perms = new CustomPermissionCollection();
            addPermissions();
        }
    }

    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        return perms;
    }

    private void addPermissions() {
        SocketPermission socketPermission = new SocketPermission("*", "connect, resolve, accept, listen");
        PropertyPermission propertyPermission = new PropertyPermission("*", "read, write");
        FilePermission filePermission = new FilePermission("<<ALL FILES>>", "read");

        perms.add(socketPermission);
        perms.add(propertyPermission);
        perms.add(filePermission);
    }
}
