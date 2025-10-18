package net.loginto.Configuration.ConfigMenager.BasicFileContent;

public class Config {
    public static String getDefaultConfigFileContent() {
        return """
                ConfigVersion: 1.0 # Version of this configuration, do not change this
          

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
                """;

                
    }
}
