package fr.xephi.authme.command;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.permission.PermissionsManager;
import fr.xephi.authme.permission.PermissionNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

//import com.timvisee.dungeonmaze.Core;
//import com.timvisee.dungeonmaze.permission.PermissionsManager;

/**
 */
public class CommandPermissions {

    /**
     * Defines the permission nodes required to have permission to execute this command.
     */
    private List<PermissionNode> permissionNodes = new ArrayList<>();
    /**
     * Defines the default permission if the permission nodes couldn't be used.
     */
    private DefaultPermission defaultPermission = DefaultPermission.NOT_ALLOWED;

    /**
     * Constructor.
     */
    public CommandPermissions() {
    }

    /**
     * Constructor.
     *
     * @param permissionNode    The permission node required to execute a command.
     * @param defaultPermission The default permission if the permission nodes couldn't be used.
     */
    public CommandPermissions(PermissionNode permissionNode, DefaultPermission defaultPermission) {
        this.permissionNodes.add(permissionNode);
        this.defaultPermission = defaultPermission;
    }

    /**
     * Constructor.
     *
     * @param permissionNodes   The permission nodes required to execute a command.
     * @param defaultPermission The default permission if the permission nodes couldn't be used.
     */
    public CommandPermissions(List<PermissionNode> permissionNodes, DefaultPermission defaultPermission) {
        this.permissionNodes.addAll(permissionNodes);
    }

    /**
     * Add a permission node required to execute this command.
     *
     * @param permissionNode The permission node to add.
     *
     * @return True on success, false on failure.
     */
    public boolean addPermissionNode(PermissionNode permissionNode) {
        // Make sure this permission node hasn't been added already
        if (hasPermissionNode(permissionNode))
            return true;

        // Add the permission node, return the result
        return this.permissionNodes.add(permissionNode);
    }

    /**
     * Check whether this command requires a specified permission node to execute.
     *
     * @param permissionNode The permission node to check for.
     *
     * @return True if this permission node is required, false if not.
     */
    public boolean hasPermissionNode(PermissionNode permissionNode) {
        return this.permissionNodes.contains(permissionNode);
    }

    /**
     * Get the permission nodes required to execute this command.
     *
     * @return The permission nodes required to execute this command.
     */
    public List<PermissionNode> getPermissionNodes() {
        return this.permissionNodes;
    }

    /**
     * Set the permission nodes required to execute this command.
     *
     * @param permissionNodes The permission nodes required to execute this command.
     */
    public void setPermissionNodes(List<PermissionNode> permissionNodes) {
        this.permissionNodes = permissionNodes;
    }

    /**
     * Get the number of permission nodes set.
     *
     * @return Permission node count.
     */
    public int getPermissionNodeCount() {
        return this.permissionNodes.size();
    }

    /**
     * Check whether this command requires any permission to be executed. This is based on the getPermission() method.
     *
     * @param sender CommandSender
     *
     * @return True if this command requires any permission to be executed by a player.
     */
    public boolean hasPermission(CommandSender sender) {
        // Make sure any permission node is set
        if (getPermissionNodeCount() == 0)
            return true;

        // Get the default permission
        final boolean defaultPermission = getDefaultPermissionCommandSender(sender);

        // Make sure the command sender is a player, if not use the default
        if (!(sender instanceof Player))
            return defaultPermission;

        // Get the player instance
        Player player = (Player) sender;

        // Get the permissions manager, and make sure it's instance is valid
        PermissionsManager permissionsManager = AuthMe.getInstance().getPermissionsManager();
        if (permissionsManager == null)
            return false;

        // Check whether the player has permission, return the result
        for (PermissionNode node : this.permissionNodes) {
            if (!permissionsManager.hasPermission(player, node, defaultPermission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the default permission if the permission nodes couldn't be used.
     *
     * @return The default permission.
     */
    public DefaultPermission getDefaultPermission() {
        return this.defaultPermission;
    }

    /**
     * Set the default permission used if the permission nodes couldn't be used.
     *
     * @param defaultPermission The default permission.
     */
    public void setDefaultPermission(DefaultPermission defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    /**
     * Get the default permission for a specified command sender.
     *
     * @param sender The command sender to get the default permission for.
     *
     * @return True if the command sender has permission by default, false otherwise.
     */
    public boolean getDefaultPermissionCommandSender(CommandSender sender) {
        switch (getDefaultPermission()) {
            case ALLOWED:
                return true;

            case OP_ONLY:
                return sender.isOp();

            case NOT_ALLOWED:
            default:
                return false;
        }
    }

    /**
     */
    public enum DefaultPermission {
        NOT_ALLOWED,
        OP_ONLY,
        ALLOWED
    }
}
