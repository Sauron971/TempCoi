package org.kyas.tempcoi;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ListenerChat implements Listener {

    TempCoi plugin;
    public ListenerChat(TempCoi plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
        assert user != null;
        boolean isPlayerExists = isPlayerExists(p);

        if (p.hasPermission("tempcoi.tempcoitimes") && p.hasPermission("coreprotect.inspect")) {
            if (message.equals("/co i")) {
                if (!isPlayerExists) {
                    insertInTable(p);
                } else {
                    updateTimesOfUse(p);
                }
            }
        }

    }

    private void updateTimesOfUse(Player p) {
        try {
            String updateUse = "UPDATE players SET timesUse = timesUse-1 WHERE name = ?";
            PreparedStatement state = TempCoi.connection.prepareStatement(updateUse);
            state.setString(1, p.getName());
            state.executeUpdate();
            String updateDate = "UPDATE players SET lastUse = datetime(CURRENT_TIMESTAMP, 'localtime') WHERE name = ?";
            PreparedStatement state2 = TempCoi.connection.prepareStatement(updateDate);
            state2.setString(1, p.getName());
            state2.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (remainedUse(p) <= 0) {
            Node nodeCoi = PermissionNode.builder("coreprotect.inspect").value(false).build();
            User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
            user.data().add(nodeCoi);
            LuckPermsProvider.get().getUserManager().saveUser(user);
        }
    }

    private int remainedUse(Player p) {
        try {
            String update = "SELECT timesUse AS count FROM players WHERE name = ?";
            PreparedStatement state = TempCoi.connection.prepareStatement(update);
            state.setString(1, p.getName());
            ResultSet rs = state .executeQuery();
            return rs.getInt("count");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private boolean isPlayerExists(Player p) {
        boolean isPlayerExists = false;
        try {
            String isUserAdded = "SELECT COUNT(*) AS count FROM players WHERE name =?";
            PreparedStatement statement = TempCoi.connection.prepareStatement(isUserAdded);
            statement.setString(1, p.getName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                isPlayerExists = count > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return isPlayerExists;
    }

    private void insertInTable(Player p) {
        try {
            String insert = "INSERT INTO players (name, lastUse, timesUse) VALUES (?, datetime(CURRENT_TIMESTAMP, 'localtime'),?)";
            PreparedStatement state = TempCoi.connection.prepareStatement(insert);
            state.setString(1, p.getName());
            state.setInt(2, 6);
            state.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
