package org;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.file.*;

public final class LoginTo extends JavaPlugin implements Listener, CommandExecutor {

    private File dataFile;
    private Map<String, UserData> userData;
    private final HashMap<UUID, Boolean> loggedInPlayers = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String[] d = {"k5Vp","dEka","ndcd","23Fa","Fhbr"};
    private final HashMap<UUID, Boolean> playerProtection = new HashMap<>();

    private File configFile;
    private File msgFile;
    private Map<String, String> config;
    private Map<String, String> msg;


    private int tentativi;







    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("register").setExecutor(this);
        getCommand("login").setExecutor(this);
        getCommand("delacc").setExecutor(this);
        getCommand("premium").setExecutor(this);
        getCommand("changepassword").setExecutor(this);
        createDataFile();


        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        createMSGFile();
        createConfigFile();

        loadMSGFile();
        loadConfigFile();
        this.tentativi = Integer.parseInt(config.get("tries"));



        //Caratteri proibiti
        if ("true".equals(config.get("required_character"))) {
            String caratteri_richiesti = config.get("characters_needed");
            char[] caratteri = caratteri_richiesti.toCharArray();
            String all_characters = new String(caratteri);
        }



    }
    private static final String[] c = {"Lo2n","Dj3a","xO2f","3ndA","Fh3d"};


    private boolean isPremiumPlayer(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void createDataFile() {
        dataFile = new File(getDataFolder(), "data.json");
        if (!dataFile.exists()) {
            try {
                getDataFolder().mkdirs();
                dataFile.createNewFile();
                userData = new HashMap<>();
                saveData();
            } catch (IOException e) {
                getLogger().severe("Impossibile creare data.json");
            }
        } else {
            loadData();
        }
    }

    private static final String[] e = {"Fhau","wDhe","Enda","aOQf","fEan"};


    private void loadData() {
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, UserData>>() {}.getType();
            userData = gson.fromJson(reader, type);
            if (userData == null) {
                userData = new HashMap<>();
            }
        } catch (IOException e) {
            getLogger().severe("Errore caricamento dati");
        }
    }

    private void saveData() {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(userData, writer);
        } catch (IOException e) {
            getLogger().severe("Errore salvataggio dati");
        }
    }
    private static final String[] f = {"2FDb","eDb2","c6es","Dh2a","2sd8"};


    public static String gck() {

        // Your 32 byte key

        return "aaaabbbbccccddddeeeeffffgggghhhh";
    }


//Message file
    private static final String[] b = {"D91w","zEVr", "C812","C82n","J894"};


    private void createMSGFile() {
        msgFile = new File(getDataFolder(), "messages.yml");

        if (!msgFile.exists()) {
            try {
                getDataFolder().mkdirs();
                msgFile.createNewFile();

                // Crea una stringa YAML che rappresenta tutti i messaggi
                String msgYaml = """
                    # Hi, welcome in the LoginTo personalization file
                    
                    # When a player successfully sing in
                    login_success: 'Login completed'
                    
                    # When a player succesfully sing up
                    register_success: 'Registration completed'
                    
                    # When the password is wrong
                    wrong_password: 'Wrong password'
                    
                    # When a player try to do the /register command but hi already have an account
                    already_registered: 'You are already registered'
                    
                    # When a player join and he need to sing in
                    login_prompt: 'Do: /login <password> to sign up'
                    
                    # When a player try to do /login or /premium commands but he is not registered
                    not_registered: 'You are not registered'
                    
                    # When a player join and ho doesn't have an account
                    register_prompt: 'Welcome! Use /register <password> <confirm_password> to register yourself'
                    
                    # This message pop up when an admin delete a user account (Admin pov)
                    account_deleted: 'Account deleted successfully'
                    
                    # When an admin delete your account (User pov)
                    admin_deleted_account: 'Your account got eliminated by an admin, rejoin to create a new one'
                    
                    # When a user try to register him self but the 2 password are not the same
                    password_mismatch: 'The passwords didnt match'
                    
                    # When a player is premium and join in the server
                    premium_login: 'Login completed with premium'
                    
                    # This message pop up when a user do /premium (User pov)
                    premium_warning: 'You are now a premium user'
                    
                    # When a user do the /premium command but it's on a cracked client
                    premium_error: 'You are not a minecraft premium user'
                    
                    # When an already premium user do the /premium command
                    premium_already: 'Premium status: activated'
                    
                    # When a non-op user do the /delacc command
                    no_permission: 'Error, you dont have permission to do this'
                    
                    # When a user have a premium account but that features is disabled
                    premium_false: 'Sorry but the premium feature is disabled, use /login <password>'
                    
                    # When a user didn't use /login correctly 
                    login_error: 'Error, do: /login <password>'
                    
                    # When a user join for the first time and you have the required characters option enabled
                    register_prompt_characters: 'Welcome! Use /register <password> <confirm_password> to register yourself, make sure to include one of these characters: '
                    
                    # When a user do /register but his password didn't have a required character
                    register_character_error: 'Your password needs at least 1 of these characters: '
                    
                    # When a user use the /register command wrong
                    register_error: 'Error, do /register <password> <confirm_password>
                    
                    # When a player change his password and get disconnected
                    change_psw_success_disconnected: 'Your password has been changed, rejoin and sign up'
                    
                    # When a player change his password, and didn't get disconnected
                    change_psw_success: 'Your password has been changed'
                    
                    # When a user didn't use /changepassword correctly 
                    correct_use_of_changepassword: 'Correct use: /changepassword <old_password> <new_password>'
                    
                    # Message to show when a player get's kicked because ho finish his tries to login
                    message_limit_end: 'You failed too many time trying to sing up, rejoin the server to retry'
                    
                    # If a user/admin write wrong the /delacc command
                    delacc_wrong_syntax: 'Usage: /delacc <username>'
                    
                    # If a user/admin write the /delacc command but the selected user doesn't exist
                    delacc_account_not_fount: 'Account not found'
                    
                    # If a player use's a command but he doesn't have permission
                    no_permission: 'You can't do this command because you don't have permission'
                    
                    # If a non-premium user join with a different premium user ip, the ip for safety reason will be hidden from anyone, NOTE: if a premium user join but he changed ip the system will still block him like a cracked player
                    login_prompt_premium_acc_hacked: 'We detected that you have a different ip, please do /login <password> to confirm your identity and change the user-ip'
                    
                    
                """;

                // Salva la stringa YAML nel file
                saveMSGFile(msgYaml);

            } catch (IOException e) {
                getLogger().severe("Errore creazionemessages.yml!");
            }
        } else {
            loadMSGFile();
        }
    }

    // Carica il file messages.yml e memorizza i dati
    private void loadMSGFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(msgFile))) {
            msg = new HashMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                // Ignora righe vuote o commenti (che iniziano con #)
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // Trova la prima occorrenza di ": " per evitare problemi con valori che contengono ":"
                int separatorIndex = line.indexOf(": ");
                if (separatorIndex != -1) {
                    String key = line.substring(0, separatorIndex).trim(); // Ottieni la chiave
                    String value = line.substring(separatorIndex + 2).trim(); // Ottieni il valore

                    // Rimuove gli apici singoli o doppi se presenti attorno al valore
                    if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    msg.put(key, value);
                }
            }
        } catch (IOException e) {
            getLogger().severe("Errore caricamento messages.yml: " + e.getMessage());
        }

    }

    // Salva il contenuto del file messages.yml
    private void saveMSGFile(String yamlContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(msgFile))) {
            writer.write(yamlContent);

        } catch (IOException e) {
            getLogger().severe("Errore salvataggio messages.yml");
        }
    }


//Config file





    private void createConfigFile() {
        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                getDataFolder().mkdirs();
                configFile.createNewFile();

                // Crea una stringa YAML che rappresenta la configurazione
                String configYaml = """
                    # Hi, welcome in the LoginTo configuration
                    
                    # Enable Loginto (true), Disable it (false)
                    loginto: 'true'
                    
                    # Let the player join with premium
                    # Hi to everyone, i'm so sorry for what append with the premium feature, i created a safe way to use the auto-login for players, also thanks to everyone for the support <3, by Yager400
                    premium: 'true'
                    
                    # When a player need's to sing up/sing in give the blindness effect?
                    blindness: 'true'
                    
                    # If a player need to register, it's password need's to have certain characters
                    required_character: false
                    
                    # If required_character is true here you need to put the required characters
                    characters_needed: ''
                    
                    # When a player change password kick him from the server?
                    kick_after_psw_change: 'true'
                    
                    # Should LoginTo kick a player if he didn't write the correct password on /login <password> ?
                    kick_if_psw_wrong: 'true'
                    
                    # How many tries a player have before get's kicked (for this config you need to enable 'kick_if_psw_wrong'
                    tries: '3'
                    #Default will be 3
                    
                    # Chose where the user get's teleported when he join, this feature is disabled by default
                    enabled_worldlocation_teleport: 'false'
                    
                    # In what dimension will the player be teleported, world type:
                    # - world = The OverWorld
                    # - world_nether = The Nether
                    # - world_the_end = The End
                    #
                    # This feature works even with custom world like skyblock, terrain generation plugin ect...
                    world: 'world'
                    
                    # Coords where the player get's teleported (for this feature you need 'enabled_worldlocation_teleport' on true)
                    x: 0
                    y: -64
                    z: 0
                    
                    # If this setting is on true, LoginTo will require to be an operator (op), if you want to use LuckPerms or any other permission plugin turn this on false
                    op_required_delacc: 'true'
                    
                    # Permission: if you want to use permission go to https://modrinth.com/plugin/loginto and read the description to find everything 
                    # Make sure that 'op_required_delacc' is set to false to use permission
                    permission: 'false'
                    
                    # When a user join he will be immune by any damage, then when he sing up this protection will be removed
                    godmode: 'true'
                    
                    # Change server when someone login in the server
                    # !!I tested this feature only with velocity so i don't know if it works with bungeecord or any other proxy
                    # To use it just put on true the features and write your server name
                    # WARNING, in this version (1.5) this feature is a bit buggy, so if you want to use this feature you will need to disable the premium command
                    # or else the premium player won't be teleported to the server
                    go_to_server: 'false'
                    
                    server_name: ''
                    
                    
                    
                    
                    # Send a webhook in a discord server, you need to put your webhook's url
                    
                    # When a player sing up
                    register_webhook: ''
                    
                    # When a player sing in
                    login_webhook: ''
                    
                    # When a player use the premium features
                    premium_webhook: ''
                    
                    # When an admin delete a user account
                    delacc_webhook: ''
                    
                    
                    
                    
                    # Next update?
                    ban_for_join: 'Next_update?'
                    ban_webhook: 'Next_update?'
                    
                    
                    
                    # When????
                    host: ""
                    port: ""
                    name: ""
                    user: ""
                    password: ""
                    
                    
                """;

                // Salva la stringa YAML nel file
                saveConfigFile(configYaml);

            } catch (IOException e) {
                getLogger().severe("Errore nella creazione del file config.yml!");
            }
        } else {
            loadConfigFile();
        }
    }

    // Carica il file config.yml e memorizza i dati
    private void loadConfigFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            config = new HashMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                int separatorIndex = line.indexOf(": ");
                if (separatorIndex != -1) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 2).trim();

                    if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    config.put(key, value);
                }
            }
        } catch (IOException e) {
            getLogger().severe("Errore caricamento config.yml: " + e.getMessage());
        }
    }

    // Salva il contenuto del file config.yml
    private void saveConfigFile(String yamlContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(yamlContent);

        } catch (IOException e) {
            getLogger().severe("Errore salvataggio config.yml");
        }
    }

    public void changeServer(Player player, String serverName) {


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());

    }





    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (playerProtection.getOrDefault(uuid, false)) {
            event.setCancelled(true);

        }
    }





    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws Exception {



        if ("true".equals(config.get("loginto"))) {


            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            loggedInPlayers.put(player.getUniqueId(), false);

            playerProtection.put(uuid, false);
            if ("true".equals(config.get("godmode"))) {
                playerProtection.put(uuid, true);
            }

            if ("true".equals(config.get("enabled_worldlocation_teleport"))) {
                String worldName = config.getOrDefault("world", "world");
                double x = Double.parseDouble(config.getOrDefault("x", "0"));
                double y = Double.parseDouble(config.getOrDefault("y", "-64"));
                double z = Double.parseDouble(config.getOrDefault("z", "0"));

                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Location location = new Location(world, x + 0.5, y, z + 0.5);
                    player.teleport(location);
                }
            }

            String bixuiabi = player.getAddress().getAddress().getHostAddress().replace("\\u003d", "=");
            loadData();

            if (userData.containsKey(player.getName())) {

                String p34871pxsw = iugcaiguy(bixuiabi);
                if (userData.get(player.getName()).isPremium().equals(p34871pxsw)) {
                    if ("true".equals(config.get("premium"))) {


                        loggedInPlayers.put(player.getUniqueId(), true);
                        //if ("true".equals(config.get("go_to_server"))) {
                        //String serverName = config.get("server_name");
                        //Thread.sleep(5000);
                        //changeServer(player, serverName);
                        //}

                        player.sendMessage(ChatColor.GREEN + msg.get("premium_login"));
                        playerProtection.put(uuid, false);

                    } else {
                        player.sendMessage(ChatColor.RED + msg.get("premium_false"));
                    }
                } else if (userData.get(player.getName()).isPremium().equals("none")) {
                    if ("true".equals(config.get("blindness"))) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, false, false));
                    }
                    loadMSGFile();
                    player.sendMessage(ChatColor.RED + msg.get("login_prompt"));
                }

                else {
                    if ("true".equals(config.get("blindness"))) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, false, false));
                    }
                    loadMSGFile();
                    player.sendMessage(ChatColor.RED + msg.get("login_prompt_premium_acc_hacked"));
                }



            } else {
                if ("true".equals(config.get("blindness"))) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, false, false));
                }
                if ("true".equals(config.get("required_character"))) {
                    String caratteri_richiesti = config.get("characters_needed");
                    char[] caratteri = caratteri_richiesti.toCharArray();
                    String all_characters = new String(caratteri);
                    player.sendMessage(ChatColor.RED + msg.get("register_prompt_characters")+ ChatColor.WHITE + all_characters + "\n" + ChatColor.GRAY + "Service offered by LoginTo on Modrinth");
                }
                else {
                    player.sendMessage(ChatColor.RED + msg.get("register_prompt") + "\n" + ChatColor.GRAY + "Service offered by LoginTo on Modrinth");
                }

            }
        }
        else {
            Player player = event.getPlayer();
            loggedInPlayers.put(player.getUniqueId(), true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!loggedInPlayers.getOrDefault(player.getUniqueId(), false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        loggedInPlayers.remove(event.getPlayer().getUniqueId());

        this.tentativi = Integer.parseInt(config.get("tries"));
    }
    private static final String[] g = {"z37H","dEga","2Coa","leas","ED2H"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String webhook_premium = config.get("premium_webhook");
        String webhook_login = config.get("login_webhook");
        String webhook_register = config.get("register_webhook");
        String webhook_delacc = config.get("delacc_webhook");
        String webhook_ban = config.get("ban_webhook");
        String serverIP = Bukkit.getIp();
        int serverPort = Bukkit.getPort();




        if (!(sender instanceof Player)) {
            sender.sendMessage("Error");
            return true;
        }
        Player player = (Player) sender;
        String playerName = player.getName();
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "register":
                String psw = args[0];

                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + msg.get("register_error"));
                    return true;
                }
                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.register")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }



                String caratteri_richiesti = config.get("characters_needed");
                char[] caratteri = caratteri_richiesti.toCharArray();
                String caratteri_totali = new String(caratteri);
                boolean trovato = false;
                for (char c : caratteri_richiesti.toCharArray()) {
                    if (psw.indexOf(c) != -1) {
                        trovato = true;
                        break;
                    }
                }


                if (args.length != 2 || !args[0].equals(args[1])) {
                    player.sendMessage(ChatColor.RED + msg.get("password_mismatch"));
                    return true;
                }
                if (userData.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + msg.get("already_registered"));
                    return true;
                }







                if (!trovato) {
                    if ("true".equals(config.get("required_character"))) {
                        player.sendMessage(ChatColor.RED + msg.get("register_character_error") + ChatColor.WHITE + caratteri_totali);
                        break;
                    }
                }
                userData.put(player.getName(), new UserData("none", args[0]));
                saveData();
                player.sendMessage(ChatColor.GREEN + msg.get("register_success"));
                if (webhook_register == null || webhook_register.isBlank()) {

                }
                if (!webhook_register.startsWith("https://discord.com/api/webhooks/")) {

                }
                else {
                    String embed_register = "{ \"embeds\": [{ \"title\": \"" + playerName + " completed the registration" +
                            "\", \"description\": \"" + playerName + " is now registered\\n" +
                            "ServerIp: " + (serverIP.isEmpty() ? "0.0.0.0" : serverIP) + ":" + serverPort +
                            "\", \"color\": 65280 }] }";
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(webhook_register).openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(embed_register.getBytes());
                            os.flush();
                        }

                        int responseCode = conn.getResponseCode();


                    } catch (IOException e) {
                        e.printStackTrace(); // Stampa l'errore per il debugging
                        getLogger().severe("Errore richiesta webhook");
                    }
                }
                break;


            case "login":
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + msg.get("login_error"));
                    return true;
                }

                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.login")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }

                UUID uuid = player.getUniqueId();
                if (!userData.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + msg.get("not_registered"));
                    return true;
                }
                if (!userData.get(player.getName()).getPassword().equals(args[0])) {
                    player.sendMessage(ChatColor.RED + msg.get("wrong_password"));


                    if ("true".equals(config.get("kick_if_psw_wrong"))) {
                        this.tentativi--;
                        if (this.tentativi == 0) {
                            player.kickPlayer(msg.get("message_limit_end"));
                            this.tentativi = Integer.parseInt(config.get("tries"));
                        }
                    }
                    return true;
                }
                loggedInPlayers.put(player.getUniqueId(), true);
                if ("true".equals(config.get("go_to_server"))) {
                    String serverName = config.get("server_name");
                    changeServer(player, serverName);
                }
                player.sendMessage(ChatColor.GREEN + msg.get("login_success"));


                if (!userData.get(player.getName()).isPremium().equals("none")) {


                    String s2h8dna = player.getAddress().getAddress().getHostAddress().replace("\\u003d", "=");


                    if (isPremiumPlayer(player.getName())) {


                        String p2s = iugcaiguy(s2h8dna);

                        userData.get(player.getName()).setPremium(p2s);
                        saveData();
                    }

                }





                if ("true".equals(config.get("blindness"))) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                }
                playerProtection.put(uuid, false);
                if (webhook_login == null || webhook_login.isBlank()) {

                }
                if (!webhook_login.startsWith("https://discord.com/api/webhooks/")) {

                }
                else {
                    String embed_login = "{ \"embeds\": [{ \"title\": \"" + playerName + " signed up" +
                            "\", \"description\": \"" + playerName + " joined the server\\n" +
                            "ServerIp: " + (serverIP.isEmpty() ? "0.0.0.0" : serverIP) + ":" + serverPort +
                            "\", \"color\": 255 }] }";
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(webhook_login).openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(embed_login.getBytes());
                            os.flush();
                        }

                        int responseCode = conn.getResponseCode();


                    } catch (IOException e) {
                        e.printStackTrace();
                        getLogger().severe("Errore richiesta webhook");
                    }
                }


                break;

            case "delacc":
                if ("true".equals(config.get("op_required_delacc"))) {
                    if (!player.isOp() || args.length != 1) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }

                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.delacc")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }

                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + msg.get("delacc_wrong_syntax"));
                    return true;
                }

                String nome = args[0];

                if (!userData.containsKey(nome)) {
                    player.sendMessage(ChatColor.RED + msg.get("delacc_account_not_fount"));
                    return true;
                }

                userData.remove(args[0]);
                saveData();
                Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    target.kickPlayer(ChatColor.RED + msg.get("admin_deleted_account"));
                }
                player.sendMessage(ChatColor.GREEN + msg.get("account_deleted"));
                String targetName = target.getName();

                if (webhook_delacc == null || webhook_delacc.isBlank()) {

                }
                if (!webhook_delacc.startsWith("https://discord.com/api/webhooks/")) {

                }
                else {
                    String embed_delacc = "{ \"embeds\": [{ \"title\": \"" + targetName + " account's got deleted" +
                            "\", \"description\": \"" + playerName + " deleted the " + targetName + " account\\n" +
                            "ServerIp: " + (serverIP.isEmpty() ? "0.0.0.0" : serverIP) + ":" + serverPort +
                            "\", \"color\": 16711680 }] }";
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(webhook_delacc).openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(embed_delacc.getBytes());
                            os.flush();
                        }

                        int responseCode = conn.getResponseCode();


                    } catch (IOException e) {
                        e.printStackTrace(); // Stampa l'errore per il debugging
                        getLogger().severe("Errore richiesta webhook");
                    }
                }
                break;





            case "premium":
                if (!userData.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + msg.get("not_registered"));
                    return true;
                }

                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.premium")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }

                String s2h8dna2 = player.getAddress().getAddress().getHostAddress().replace("\\u003d", "=");

                if (!userData.get(player.getName()).isPremium().equals("none")) {
                    player.sendMessage(ChatColor.YELLOW + msg.get("premium_already"));
                    return true;
                }

                if (isPremiumPlayer(player.getName())) {


                    String p2s = iugcaiguy(s2h8dna2);

                    userData.get(player.getName()).setPremium(p2s);
                    saveData();
                    player.sendMessage(ChatColor.GREEN + msg.get("premium_warning"));
                    if (webhook_premium == null || webhook_premium.isBlank()) {

                    }
                    if (!webhook_premium.startsWith("https://discord.com/api/webhooks/")) {

                    }
                    else {
                        String embed_premium = "{ \"embeds\": [{ \"title\": \"" + playerName + " joined with premium\", " +
                                "\"description\": \"" + playerName + " joined in your server using the premium features\\n" +
                                "ServerIp: " + (serverIP.isEmpty() ? "0.0.0.0" : serverIP) + ":" + serverPort + "\", " +
                                "\"color\": 16776960 }] }";

                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL(webhook_premium).openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            try (OutputStream os = conn.getOutputStream()) {
                                os.write(embed_premium.getBytes());
                                os.flush();
                            }

                            int responseCode = conn.getResponseCode();

                        } catch (IOException e) {
                            e.printStackTrace(); // Stampa l'errore per il debugging
                            getLogger().severe("Errore richiesta webhook");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + msg.get("premium_error"));
                }
                break;


            case "changepassword":

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + msg.get("correct_use_of_changepassword"));
                    return true;
                }

                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.changepassword")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }



                if (!userData.containsKey(playerName)) {
                    player.sendMessage(ChatColor.RED + msg.get("not_registered"));
                    return true;
                }

                String oldPassword = args[0];
                String newPassword = args[1];
                UserData playerData = userData.get(playerName);

                if (!playerData.getPassword().equals(oldPassword)) {
                    player.sendMessage(ChatColor.RED + msg.get("wrong_password"));
                    return true;
                }

                playerData.password = newPassword;
                saveData();

                if ("true".equals(config.get("kick_after_psw_change"))) {
                    player.kickPlayer(ChatColor.GREEN + msg.get("change_psw_success_disconnected"));
                }
                else {
                    player.sendMessage(ChatColor.GREEN + msg.get("change_psw_success"));
                }


                return true;

            case "loginto-reload":

                if ("true".equals(config.get("permission"))) {
                    if (!player.hasPermission("loginto.reload")) {
                        player.sendMessage(ChatColor.RED + msg.get("no_permission"));
                        return true;
                    }
                }


                loadData();
                loadConfigFile();
                loadMSGFile();

                player.sendMessage("§2LoginTo reloaded");
        }
        return true;
    }
    private static final String[] h = {"Jael","FDha","32Vh","NBhe","8LVt"};

    public static String iugcaiguy(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(gck().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] void1234 = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(void1234);
        } catch (Exception e) {
            return "ERRORE_CIFRAZIONE";
        }
    }




    private static final String[] a = {"nms2","s21d","A2sA","rSvw","sdA3"};




    private static class UserData {
        public String ip;
        private String premium;
        private String password;

        public UserData(String premium, String password) {
            this.premium = premium;
            this.password = password;
        }

        public String isPremium() {
            return premium;
        }

        public void setPremium(String premium) {
            this.premium = premium;
        }

        public String getPassword() {
            return password;
        }
    }
}
