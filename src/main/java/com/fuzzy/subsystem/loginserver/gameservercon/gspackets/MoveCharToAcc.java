package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.database.DatabaseUtils;
import com.fuzzy.subsystem.database.FiltredPreparedStatement;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.database.ThreadConnection;
import com.fuzzy.subsystem.loginserver.L2LoginClient;
import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;
import com.fuzzy.subsystem.loginserver.gameservercon.lspackets.MoveCharToAccResponse;
import com.fuzzy.subsystem.util.Log;

import java.sql.ResultSet;
import java.util.logging.Logger;

/**
 * @Author: Abaddon
 */
public class MoveCharToAcc extends ClientBasePacket {
    private static final Logger log = Logger.getLogger(MoveCharToAcc.class.getName());

    public MoveCharToAcc(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    @Override
    public void read() {
        String player = readS().trim();
        String oldacc = readS().trim();
        String newacc = readS().trim();
        String pass = readS().trim();

        String dbPassword = null;
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            try {
                statement = con.prepareStatement("SELECT * FROM accounts WHERE login = ?");
                statement.setString(1, oldacc);
                rs = statement.executeQuery();
                if (rs.next())
                    dbPassword = rs.getString("password");
            } catch (Exception e) {
                log.warning("Can't recive password for account " + oldacc + ", exciption :" + e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            try {
                statement = con.prepareStatement("SELECT * FROM accounts WHERE login = ?");
                statement.setString(1, newacc);
                rs = statement.executeQuery();
                if (!rs.next()) {
                    sendPacket(new MoveCharToAccResponse(player, 1));
                    return;
                }
            } catch (Exception e) {
                log.warning("Can't recive password for account " + oldacc + ", exciption :" + e);
            } finally {
                DatabaseUtils.closeDatabaseSR(statement, rs);
            }

            //Encode old password and compare it to sended one, send packet to determine changed or not.
            try {
                if (!LoginController.DEFAULT_CRYPT.compare(pass, dbPassword))
                    sendPacket(new MoveCharToAccResponse(player, 0));
                else {
                    statement = con.prepareStatement("UPDATE accounts SET login = ? WHERE login = ?");
                    statement.setString(1, newacc);
                    statement.setString(2, oldacc);
                    int result = statement.executeUpdate();
                    L2LoginClient client = LoginController.getInstance().getAuthedClient(oldacc);
                    if (result != 0)
                        Log.add("<old=\"" + oldacc + "\" new=\"" + newacc + "\" ip=\"" + (client != null ? client.getIpAddress() : "0.0.0.0") + "\" />", "accounts");

                    sendPacket(new MoveCharToAccResponse(player, 2));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                DatabaseUtils.closeStatement(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeConnection(con);
        }
    }
}
