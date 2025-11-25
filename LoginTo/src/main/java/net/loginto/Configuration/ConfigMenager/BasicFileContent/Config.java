package net.loginto.Configuration.ConfigMenager.BasicFileContent;

public class Config {
    public static String getDefaultConfigFileContent() {
        return """
                ConfigVersion: 1.1 # Version of this configuration, do not change this
          

                kick-rules:
                    
                    # When a player change password kick him from the server?
                    kick_on_password_change: true
                

                    # THIS DOESNT WORK FOR NOW
                    # Should LoginTo kick a player if he didn't write the correct password on /login <password> ?
                    kick_on_wrong_password: true
                    
                    tries: 3
                

                world:
                
                    # Chose where the user get's teleported when he join, this feature is disabled by default
                    enabled_world_location_teleport: false
                    
                    # In what dimension will the player be teleported, vanilla world type:
                    # - world = The OverWorld
                    # - world_nether = The Nether
                    # - world_the_end = The End
                    world: 'world'
                    
                    # Coords where the player get's teleported (for this feature you need 'enabled_world_location_teleport' on true)
                    x: 0
                    y: -64
                    z: 0
                

                password-security:
                
                    # If a player need to register, it's password need's to have certain characters
                    required_character: false
                
                    # If required_character is true here you need to put the required characters
                    characters_needed: ''
                

                permissions:
                
                    # If this setting is on true, LoginTo will require to be an operator (op), if you want to use LuckPerms or any other permission plugin turn this on false
                    op_required_delacc: true
                
                    # Permission: if you want to use permission go to https://modrinth.com/plugin/loginto and read the description to find everything 
                    # Make sure that 'op_required_delacc' is set to false to use permission
                    permission: false
                
                
                player-effects:
                    
                    # When a user join he will be immune by any damage, then when he sing up this protection will be removed
                    godmode: true
                
                    # When a player need's to sing up/sing in give the blindness effect?
                    blindness: true
                

                proxy-integration:
                
                    # Change server when someone login in the server
                    # !!I tested this feature only with velocity so i don't know if it works with bungeecord or any other proxy
                    # To use it just put on true the features and write your server name
                
                    go_to_server: false
                
                    server_name: ''
                
            
                discord-webhook:
                    # Send a webhook in a discord server, you need to put your webhook's url
                
                    # When a player sing up
                    register_webhook: ''
                
                    # When a player sing in
                    login_webhook: ''
                    
                    # When an admin delete a user account
                    delacc_webhook: ''

                    # When someone changes their password
                    changepassword_webhook: ''

                support:
                    # If you want to support me please leave this on, otherways no problem, thank you anyways
                    # This setting will remove the 'Service offered by LoginTo on Modrinth' watermark
                    loginto-watermark: true


                data:
                    # How do you want to save the player's login information?
                    # json -> the classic way LoginTo uses to save player data
                    # sqlite -> use SQLite to save player data; faster than JSON and more secure
                    # mysql -> use MySQL to save player data; a classic networked database
                    # postgre -> use PostgreSQL to save player data; faster with large amounts of data
                    data-saving-type: "json"

                
                database:
                    # If you are saving the datas with the option "json" do not touch this part, istead if you are using sqlite just compile the name param
                    # If you are having problems please report those to my github project -> https://github.com/Yager400/LoginTo/issues

                    # The ip address of your database, if you are hosting it on the same machine as your server type "localhost" or "127.0.0.1"
                    host: ""

                    # The port of your database, the default MySQL port is "3306" while the default PostgreSQL port is "5432"
                    port: 0

                    # The name of your database, to create this go to your dbms and type this "create database <your_db_name>;" and run (if the database doesn't exist LoginTo will try to create it but probably it wont work)
                    name: ""

                    # The username to access your database
                    user: ""

                    # The password to access your database
                    password: ""
                """;

                
    }
}
