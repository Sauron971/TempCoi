package org.kyas.tempcoi;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckTime implements Runnable {
    @Override
    public void run() {
        try {
            String update = "UPDATE players SET timesUse = 6 WHERE ROUND((JULIANDAY('now', 'localtime') - JULIANDAY(lastUse)) * 86400) >= 1200 RETURNING name;";
            PreparedStatement state = TempCoi.connection.prepareStatement(update);

            ResultSet resultSet = state.executeQuery();
            while (resultSet.next()) {
                String playerName = resultSet.getString("name");
                Player p = Bukkit.getPlayer(playerName);
                Node nodeCoi = PermissionNode.builder("coreprotect.inspect").value(true).build();
                assert p != null;
                User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
                user.data().add(nodeCoi);
                LuckPermsProvider.get().getUserManager().saveUser(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
