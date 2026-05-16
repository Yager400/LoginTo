![LoginTo](https://cdn.modrinth.com/data/cached_images/9d4250ca2166d5977633f4c456472eb6e43a178a.png)

## **What is LoginTo?**
LoginTo is a fully customizable authentication plugin for your Minecraft servers or network.  
It offers complete control over messages, along with a feature-rich configuration file that allows you to easily adjust and personalize every aspect of the plugin.

## **Why choose LoginTo?**
LoginTo is simple to set up while still providing powerful features, including:
- Premium authentication (auto-login)
- Secure password hashing using BCrypt
- Detailed login and registration logging
- Advanced configuration options

It is built to help you keep your Minecraft server or network secure without sacrificing ease of use.

## **Updates**
LoginTo is actively maintained and regularly updated with bug fixes and new features.  
For version 3.x, updates will primarily focus on stability improvements and performance optimizations.

# Plugin

<details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">Configuration</summary>

  ```Yaml 
    ConfigVersion: "1.10" # DO NOT CHANGE THIS
    # -------------------------------------------------------------------------------------------------- #
    #                                                                                                    #
    #                                       LoginTo Configuration                                        #
    #                                                                                                    #
    # -------------------------------------------------------------------------------------------------- #


    commands-settings:
      # Enter here the commands that will be allowed before logging in (the /login, /register, and /changepassword commands will always be active; aliases like /l and /r must be added below)
      pre-login-allowed-commands:
        - "l"
        - "r"

    auth-security:

      # Kick the player if they enter an incorrect password during /login?
      kick-on-invalid-password: true
      # Maximum login attempts allowed before getting kicked
      max-login-attempts: 3

      # Kick players who remain unauthenticated for too long?
      kick-on-auth-timeout: true
      # Seconds allowed to authenticate before the kick occurs
      auth-timeout-seconds: 10

    password-requirements:
      # Require specific special characters in the password during registration?
      require-special-chars: false
      # List the required characters if the setting above is set to true
      required-char-list: ''

      # Define password length constraints
      length-check:
        enabled: true
        min-length: 8
        max-length: 32

      # Settings for too common passwords
      banned-password:
        # Decline any loginto action if the password is too simple
        decline-on-common-password: true

                # Use the RockYou password list (if this is enabled, the plugin will download the txt file from this link: https://weakpass.com/download/90/rockyou.txt.gz)
        # That file uncompressed will be around 130MB
        use-rockyou: true

                # If you want to add some banned password, put them here
        # The placeholder '%username%' is to define the player name
        banned-password:
          - "%username%"

    otp-config:

      # Set here the server name that the player will see in the authentication app for the code.
      # Note: This is not the Bungee/Velocity server name, but the public server name (e.g., Hypixel or Mineman).
      # Use only URL-friendly characters
      server-name: "MyServer"


    # World and teleportation
    spawn-settings:
      # Enable automatic teleportation to a specific location upon joining?
      teleport-on-join: false

      # Target dimension for the teleport (e.g., world, world_nether, world_the_end)
      target-dimension: 'world'

      # Exact coordinates for the teleportation point
      spawn-coordinates:
        x: 0
        y: -64
        z: 0

      # Return the player to their last known location after a successful login?
      restore-previous-location: true


    # Integrations and webhooks
    integrations:

      proxy:
        # Specify the server where players should be sent after logging into your network (e.g., 'lobby-1'). 
        # If you are not using a proxy or wish to disable this feature, leave this field empty.
        server-post-login: ""

      discord:
        # Webhook URLs and custom messages (Supports Discord Markdown & PAPI)
        # Available variables: %playerName%, %targetPlayer%

        register-webhook-url: ''
        register-message: "**%playerName%** has completed the registration"

        login-webhook-url: ''
        login-message: "**%playerName%** has logged in"

        unregister-webhook-url: ''
        unregister-message: "**%playerName%** has deleted **%targetPlayer%**'s account"

        password-change-webhook-url: ''
        password-change-message: "**%playerName%** has changed their password"


    # Storage
    storage:

      # Supported storage methods: sqlite, mysql, postgre, h2
      # Changing this option requires a full server reboot
      storage-type: "sqlite"

      database:
        # Connection details for the database (for sqlite or h2, you will only need to change the name)
        host: "localhost"
        port: 3306
        name: "LoginTo_DB"
        user: ""
        password: ""


    # Premium system (Original accounts)
    premium:
      # Enables AutoLogin, /premium, and /cracked commands, and makes proxy commands unusable until the user is logged in.
      # Requires a Proxy (Velocity/Bungee) with the plugin installed. If you are running the proxy and the server on 
      # two different machines or using a dedicated host with Pterodactyl, you must use MySQL.
      enable-premium-features: false

      storage:
        # Database type for cross-server communication (mysql or h2). MySQL is highly recommended if available.
        database-type: "h2"

      database:
        host: "localhost"
        # If the port is set to 0, it will use the database's default port (3306 for MySQL and 9092 for H2)
        port: 0
        user: "sa"
        password: ""
        # Only used for MySQL; H2 defaults to 'LoginTo_Sharing'
        database-name: "LoginTo_Sharing"

    logging:
      # This feature logs all players who join the server
      logging: true

      # The date and time format for displaying logs
      # DO NOT use spaces " " within the time format. You can use "HH:mm:ss-dd/MM/yyyy", but NOT "HH mm ss dd MM yyyy".
      # If you wish to use spaces, instruct your staff to use only the /getlogs <user> command without specifying the day.
      # Note: If you use the /getlogs command with the time selector, the date format must strictly remain "dd/MM/yyyy".
      date-format: "HH:mm:ss dd/MM/yyyy"



    plugin-utility:
      # Check for new updates on startup and notify the console?
      enable-update-checker: true

      # Show the 'Service offered by LoginTo' watermark? If you want to support my work, please consider leaving this enabled.
      show-watermark: true

      # If set to false, the plugin will require PacketEvents to be installed as a separate plugin on the server.
      # If set to true, the plugin will use the built-in PacketEvents API.
      # Disclaimer: If possible, I recommend setting this to 'false' and installing the dedicated PacketEvents plugin. 
      # Doing so helps prevent any internal compatibility issues regarding PacketEvents within this plugin.
      use-built-in-packetevents-api: true
```
</details>

<details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">Messages</summary>
  
```Yaml 
    MessageVersion: "1.7" # DO NOT CHANGE THIS
    # -------------------------------------------------------------------------------------------------- #
    #                                                                                                    #
    #                                       LoginTo Messages Config                                      #
    #                                                                                                    #
    # -------------------------------------------------------------------------------------------------- #

    # These messages support PlaceholderAPI (if installed) and MiniMessage format.

    register:
      # Messages sent during the registration process

      error:

        # Sent when a player uses the /register command but is already registered
        already-registered: "<red>You are already registered!"

        # Sent when the chosen password does not contain the required special characters
        register-character-error: "<red>Your password must contain these characters: %characters%"

        # Sent when the password length does not meet the requirements
        password-length: "<red>Your password must be between %min_length% and %max_length% characters long."

        # Sent when the confirmation password does not match the first password entered
        password-mismatch: "<red>The passwords do not match!"

        # Sent when the command syntax is incorrect
        register-usage: "<red>Error: Use /register <password> <confirm_password>"

        password-too-simple: "<red>This password is too simple, chose another one"


      # Prompt shown to unregistered players when they join the server
      register-prompt: "<dark_aqua>Welcome! Use /register <password> <confirm_password> to register."

      # Prompt shown to unregistered players when specific characters are required in the password
      register-prompt-characters: "<dark_aqua>Welcome! Use /register <password> <confirm_password> to register. Make sure to include one of these characters: %characters%"

      # Sent when a player successfully registers their account
      register-success: "<green>Registration completed successfully! Have fun on the server."


    login:
      # Messages sent during the login process

      error:

        # Sent when a player tries to log in but does not have an account yet
        not-registered: "<red>You are not registered yet!"

        # Sent when a player tries to log in but is already authenticated
        already-logged-in: "<red>You are already logged in."

        # Sent when the command syntax is incorrect
        login-usage: "<red>Error: Use /login <password>"

        # Sent when the player enters an incorrect password
        wrong-password: "<red>Incorrect password."


      # Prompt shown to registered players when they join the server
      login-prompt: "<dark_aqua>Please use /login <password> to authenticate."

      # Sent when a player successfully logs in
      login-success: "<green>Login successful! Welcome back."


    unregister:
      # Messages for the account deletion command (/delacc)

      error:

        # Sent when the specified player cannot be found in the system
        player-doesnt-exist: "<red>That player does not exist."

        # Sent when the administrator fails to type 'confirm' at the end of the command
        unregister-not-confirmed: "<red>Error: Please type 'confirm' after the player name."

        # Sent when the command syntax is incorrect
        unregister-usage: "<red>Usage: /delacc <username> <confirm>"


      # Sent to the administrator after successfully deleting an account
      account-unregistered: "<green>Account unregistered successfully."

      # Sent to the player when their account is deleted by an admin
      admin-unregistered-account: "<red>Your account was deleted by an administrator. Please rejoin to create a new one."


    # Password length and complexity rules are shared with the register command and are only listed there.
    changepassword:
      # Messages for changing the account password

      error:

        # Sent when the command syntax is incorrect
        changepassword-usage: "<red>Usage: /changepassword <newPassword> <OTPCode>"

        # Sent when the provided OTP (Two-Factor) code is incorrect
        otp-code-wrong: "<red>The OTP code is incorrect."

        # Sent when the player tries to change their password but hasn't set up OTP yet
        no-otp-code: "<red>You do not have an OTP code set up. Generate one using /otp"

        # Send when a player tries to join in someone's qrcode world
        qrcode-world-access-denied: "<red>You can't access other players qrcode world"

      # Sent when the password is successfully updated, forcing a reconnect for security
      password-changed: "<green>Your password has been changed. Please rejoin and log in again."


    cracked:
      # Messages for the cracked status command

      error:

        # Sent when the player is already marked as a cracked user
        already-cracked: "<red>You are already marked as a cracked user."

      # Warning displayed when a player executes the /cracked command
      cracked-warn: "<bold><gold>WARNING:</gold></bold><red> You are about to toggle your account to cracked. This protects your name from premium users trying to claim your account.\nType <green>/cracked</green> again to confirm."

      # Sent when the cracked status is successfully applied
      cracked-done: "<green>Your account status has been set to cracked."


    premium:
      # Messages for the premium status command

      error:

        # Sent when the player is already marked as a premium user
        already-premium: "<red>You are already a premium user."

      # Warning displayed when a player executes the /premium command
      premium-warn: "<bold><gold>WARNING:</gold></bold><red> If you switch to premium status, you might lose access to your current progress if your Minecraft account names do not match perfectly.\nType <green>/premium</green> again to confirm."

      # Sent when the premium status is successfully applied
      premium-done: "<green>Your account status has been set to premium."


    otp:
      # Messages for the One-Time Password / 2FA setup command

      error:

        # Sent when a player tries to generate a new OTP secret when they already have one configured
        otp-already-created: "<red>You cannot recreate your OTP code. Please contact an administrator to unregister your account if needed."

        # Sent when an unregistered player tries to use the OTP command
        otp-request-without-account: "<red>You must register an account before setting up OTP."

      # Safety warning shown before displaying the sensitive 2FA setup data
      otp-allert: "<bold><gold>WARNING:</gold></bold><red> This feature allows you to change your password without logging in. Ensure you are not sharing your screen with anyone. You will receive a QR code to scan with an authenticator app (e.g., Google Authenticator).\nIf it is safe to proceed, type <green>/otp</green> again to generate your code."

      otp-world:
        # Message displayed alongside the maps/QR code in-game
        otp-periodic-message: "<bold><gold>WARNING:</gold></bold><red> Scan this QR code with your authenticator app. Once done, you may safely leave and rejoin the server."


    errors:
      # General errors and restriction messages

      # Errors unrelated to specific authentication events
      general:

        # Sent when a player lacks the required permission node for a command
        no-permission: "<red>You do not have permission to execute this command."

        # Sent when a feature is disabled in the main configuration file
        feature-not-enabled: "<red>This feature is currently disabled."

      # Restrictions applied before full authentication
      activity-before-login:

        # Sent when an unauthenticated player tries to run a restricted command
        oncommand-when-not-authenticated: "<red>Please authenticate yourself before using commands."

        # Sent when an unauthenticated player tries to type in chat
        chatting-before-login: "<red>You must log in before you can use the chat."

      # Kick messages related to authentication failures
      login-fail:

        # Kick message sent when exceeding the maximum login attempts
        onkick-for-failed-login: "<red>Too many failed login attempts. Please rejoin and try again."

        # Kick message sent when the authentication timer expires
        onkick-for-long-waiting: "<red>Authentication timed out. Please rejoin and try again."

        # Kick message sent when a player attempts to join while their username is already online
        onkick-for-joining-with-same-name: "<red>Another player with your name is already online on this server."
```
</details>

<details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">
    Commands
  </summary>

  <div style="margin-top: 10px">

  <pre>/register &lt;password> &lt;confirmPassword></pre>
  <p>Registers a new account.<br>
  <strong>Aliases:</strong> /r, /reg</p>
  <hr>

  <pre>/login &lt;password></pre>
  <p>Logs into your account.<br>
  <strong>Aliases:</strong> /l</p>
  <hr>

  <pre>/delacc &lt;player> &lt;confirm></pre>
  <p>Deletes a player's account.<br>
  <strong>Permission:</strong> OP (by default)</p>
  <hr>

  <pre>/changepassword &lt;oldPassword> &lt;newPassword></pre>
  <p>Changes your account password.</p>
  <hr>

  <pre>/premium [player]</pre>
  <p>Sets yourself or another player as a premium user.</p>
  <hr>

  <pre>/cracked</pre>
  <p>Sets your account as cracked (disables premium validation).</p>
  <hr>

  <pre>/getlogs &lt;player> [date: dd/MM/yyyy]</pre>
  <p>Displays login and registration logs for a player.</p>
  <hr>

  <pre>/otp</pre>
  <p>Enable the OTP code command.</p>

  </div>
</details>

<details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">
    Permissions
  </summary>

  <div style="margin-top: 10px">

  <pre>loginto.register</pre>
  <p>Allows players to use the <code>/register</code> command.<br>
  <strong>Default:</strong> Everyone</p>
  <hr>

  <pre>loginto.login</pre>
  <p>Allows players to use the <code>/login</code> command.<br>
  <strong>Default:</strong> Everyone</p>
  <hr>

  <pre>loginto.delacc</pre>
  <p>Allows players to delete accounts using <code>/delacc</code>.<br>
  <strong>Default:</strong> OP</p>
  <hr>

  <pre>loginto.changepassword</pre>
  <p>Allows players to change their password with <code>/changepassword</code>.<br>
  <strong>Default:</strong> Everyone</p>
  <hr>

  <pre>loginto.premium.me</pre>
  <p>Allows players to set their own account as premium.<br>
  <strong>Default:</strong> Everyone</p>
  <hr>

  <pre>loginto.premium.other</pre>
  <p>Allows setting other players as premium using <code>/premium &lt;player></code>.<br>
  <strong>Default:</strong> OP</p>
  <hr>

  <pre>loginto.cracked</pre>
  <p>Allows players to switch their account to cracked mode.<br>
  <strong>Default:</strong> Everyone</p>
  <hr>

  <pre>loginto.getlogs</pre>
  <p>Allows viewing login and registration logs with <code>/getlogs</code>.<br>
  <strong>Default:</strong> OP</p>

  <hr>
  <pre>loginto.flag-banned-client</pre>
  <p>For the players with this permission, they will receive a message with the specific client of a player (if that client is flagged).<br>
  <strong>Proxy permission</strong></p>

  <hr>
  <pre>loginto.otp</pre>
  <p>Allows players to use the <code>/otp</code> command.<br>
  <strong>Default:</strong> Everyone</p>

  </div>
</details>


## **Images**
  _Register command prompt_<br>
  ![Register](https://cdn.modrinth.com/data/A5foNgax/images/cb2364a5cc1e7c8faa68e1591e4b9fad0211b9a5.png)<br>
  _Login command prompt_<br>
  
  ![Login](https://cdn.modrinth.com/data/A5foNgax/images/2c7d60e5928a1b0d66ae08ac182135976ff143f7.png)<br>
  
  _100% fully customizable messages, from text to colors, also the plugin do **not** require a reload for applying this message, you will just need to save the file_<br>
  
  ![Message](https://cdn.modrinth.com/data/A5foNgax/images/33ff6f43640a172fca1d49ee2052cba2db08523f.png)<br>


_Bstats from version 2.1_
[![BStats](https://bstats.org/signatures/bukkit/LoginTo.svg)](https://bstats.org/plugin/bukkit/LoginTo/28083)
