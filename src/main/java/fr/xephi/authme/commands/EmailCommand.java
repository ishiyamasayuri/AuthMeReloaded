package fr.xephi.authme.commands;

import java.security.NoSuchAlgorithmException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.ConsoleLogger;
import fr.xephi.authme.cache.auth.PlayerAuth;
import fr.xephi.authme.cache.auth.PlayerCache;
import fr.xephi.authme.security.PasswordSecurity;
import fr.xephi.authme.security.RandomString;
import fr.xephi.authme.settings.Messages;
import fr.xephi.authme.settings.Settings;

/**
 * @author Xephi59
 */
public class EmailCommand implements CommandExecutor {

    public AuthMe plugin;
    private Messages m = Messages.getInstance();

    public EmailCommand(AuthMe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label,
                             String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (!plugin.authmePermissible(sender, "authme." + label.toLowerCase())) {
            m.send(sender, "no_perm");
            return true;
        }

        Player player = (Player) sender;
        String name = player.getName();

        if (args.length == 0) {
            m.send(player, "usage_email_add");
            m.send(player, "usage_email_change");
            m.send(player, "usage_email_recovery");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 3) {
                m.send(player, "usage_email_add");
                return true;
            }
            plugin.management.performAddEmail(player, args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("change")) {
            if (args.length != 3) {
                m.send(player, "usage_email_change");
                return true;
            }
            plugin.management.performChangeEmail(player, args[1], args[2]);
        }
        if (args[0].equalsIgnoreCase("recovery")) {
            if (args.length != 2) {
                m.send(player, "usage_email_recovery");
                return true;
            }
            if (plugin.mail == null) {
                m.send(player, "error");
                return true;
            }
            if (plugin.database.isAuthAvailable(name)) {
                if (PlayerCache.getInstance().isAuthenticated(name)) {
                    m.send(player, "logged_in");
                    return true;
                }
                try {
                    RandomString rand = new RandomString(Settings.getRecoveryPassLength);
                    String thePass = rand.nextString();
                    String hashnew = PasswordSecurity.getHash(Settings.getPasswordHash, thePass, name);
                    PlayerAuth auth;
                    if (PlayerCache.getInstance().isAuthenticated(name)) {
                        auth = PlayerCache.getInstance().getAuth(name);
                    } else if (plugin.database.isAuthAvailable(name)) {
                        auth = plugin.database.getAuth(name);
                    } else {
                        m.send(player, "unknown_user");
                        return true;
                    }
                    if (Settings.getmailAccount.equals("") || Settings.getmailAccount.isEmpty()) {
                        m.send(player, "error");
                        return true;
                    }

                    if (!args[1].equalsIgnoreCase(auth.getEmail()) || args[1].equalsIgnoreCase("your@email.com") || auth.getEmail().equalsIgnoreCase("your@email.com")) {
                        m.send(player, "email_invalid");
                        return true;
                    }
                    auth.setHash(hashnew);
                    plugin.database.updatePassword(auth);
                    plugin.mail.main(auth, thePass);
                    m.send(player, "email_send");
                } catch (NoSuchAlgorithmException | NoClassDefFoundError ex) {
                    ConsoleLogger.showError(ex.getMessage());
                    m.send(sender, "error");
                }
            } else {
                m.send(player, "reg_email_msg");
            }
        }
        return true;
    }
}
