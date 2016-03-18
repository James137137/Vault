/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author James
 */
public class Economy_LolCon extends AbstractEconomy {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "LolCon Economy";
    private Plugin plugin = null;
    private Main lolCon = null;

    public Economy_LolCon(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (lolCon == null) {
            Plugin lolConPlugin = plugin.getServer().getPluginManager().getPlugin("Essentials");
            if (lolConPlugin != null && lolConPlugin.isEnabled()) {
                lolCon = (Main) lolConPlugin;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (lolCon == null) {
            return false;
        } else {
            return lolCon.isEnabled();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBalance(String playerName) {
        double balance;

        try {
            balance = nz.co.lolnet.lolnetapi.lolcon.lolCon.getPlayerBalance(playerName.toLowerCase());
        } catch (Exception e) {
            balance = 0;
        }

        return balance;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        try {
            return nz.co.lolnet.lolnetapi.lolcon.lolCon.registerNewPlayer(null, playerName);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
        double balance;

        balance = getBalance(playerName);
        if (balance >= amount) {
            balance = balance - amount;
            new ThreadChangeBalance(playerName, (int) -amount, "Vaults API -" + Config.SERVER_NAME + "-" + playerName);
            return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw more than balance");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support does not support deposit of funds");
    }

    public class EconomyServerListener implements Listener {

        Economy_LolCon economy = null;

        public EconomyServerListener(Economy_LolCon economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.lolCon == null) {
                Plugin essentials = event.getPlugin();

                if (essentials.getDescription().getName().equals("Essentials")) {
                    economy.lolCon = (Main) essentials;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.lolCon != null) {
                if (event.getPlugin().getDescription().getName().equals("Essentials")) {
                    economy.lolCon = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

    }

    @Override
    public String format(double amount) {
        return (int) amount + " Lolnet Coins";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) - amount >= 0;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "LolCon Eco does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return nz.co.lolnet.lolnetapi.lolcon.lolCon.playerExists(null, playerName.toLowerCase());
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

}
