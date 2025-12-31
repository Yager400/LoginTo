![LoginTo](https://cdn.modrinth.com/data/cached_images/9d4250ca2166d5977633f4c456472eb6e43a178a.png)

## **What is LoginTo?**
  LoginTo is a fully customizable plugin for player autentication, this plugin have a fully personalization about any messages and a config file full of features to explore, use and change

## **Why LoginTo?**
  LoginTo is a very easy authentication plugin to setup, featuring things like the premium authentication (auto-login), logging, and much more to protect your server.

## **Server type:**
  It works on spigot, paper, purpur, velocity and bungeecord<br>
  This plugin require **_Java 8 or higher_**  to run (velocity or bungee will need a newer java version version to run, the plugin still uses java 8)<br>

## **Commands:**
  ``/login <password>`` --> Sing up in your account<br>
  ``/register <password> <confirm_password>`` --> Register yourself to the server<br>
  ``/delacc <user>`` --> **Admin Only**  Delete a user account <br>
  ``/changepassword <old_password> <new_password>`` --> Let a player to change his password<br>
  ``/premium`` -> Lock your account from cracked user, when this command it's used LoginTo will use the auto-login, and the velocity/bungee plugin won't let cracked account with the same name pass<br>
  ``/premium <user>`` -> ADMIN ONLY: set the status to an account<br>
  ``/cracked`` -> Lock the cracked account, forcing the LoginTo authentication even if the name is premium<br>
  ``/getlogs <user> [optional]<dd/MM/yyyy>`` -> Get the server LoginTo's logs for that player, if you use the second argument the logs will be only for that day
  

## **News**
**Version 2.5**<br>
Fixed the **Required characters** feature<br>

___

Changed the messages in those params:<br>
- ``register_prompt_characters``<br>
- ``register_character_error``

The required characters are now displayed in the message using the ``%characters%`` placeholder<br>
___

Added the **Anti join-spam** feature, now if a player joins in the network too fast it will be banned for a specific ammount of seconds<br>

___

Fixed the **item pickup event**. Now, if someone isn’t logged in, they won’t be able to pick up an item, and the console won’t generate an error when something enters a hopper

___

Added the **players logs** feature

___

Added command ``/getlogs <username> [optional]<dd/MM/yyyy>``, if you do **/getlogs notch** you will get the time and date when he joins + if the authentication is premium or not, if you use **/getlogs notch 12/31/2025** you will get the logs for that player in that day

___

Added permissions for **/getlogs** command:<br>
- ``loginto.getlogs`` (By default is set to op)

## **Permission**
I tested those permission with **LuckPerms**

``loginto.register`` --> Permission for register command<br>
``loginto.login`` --> Permission for login command<br>
``loginto.delacc`` --> Permission for delacc command<br>
``loginto.changepassword`` --> Permission for changepassword command<br>
``loginto.premium.me`` -> The permission for **using the /premium command,** this is set to everyone by default (but you can change with luckperms)<br>
``loginto.premium.other`` -> The permission for **making an account premium**, this is set to only OP by default<br>
``loginto.cracked.me`` -> The permission for **using the /cracked** command, this is set to everyone by default<br>
``loginto.getlogs`` -> The permission for using the **/getlogs** command<br>

## **How to customize the text?**
  Go in the plugin folder, open the message.yml and change the message text

## **Next?**
  I don't know for now, i will check the github repo if any issue is send if i don't have any ideas
  
## **Social:**
  For anything that you need for this plugin you can go on [github issues](https://github.com/Yager400/LoginTo/issues)

## **Images**
  _Register command prompt_<br>
  ![Register](https://cdn.modrinth.com/data/A5foNgax/images/cb2364a5cc1e7c8faa68e1591e4b9fad0211b9a5.png)<br>
  _Login command prompt_<br>
  
  ![Login](https://cdn.modrinth.com/data/A5foNgax/images/2c7d60e5928a1b0d66ae08ac182135976ff143f7.png)<br>
  
  _100% fully customizable messages, from text to colors, also the plugin do **not** require a reload for applying this message, you will just need to save the file_<br>
  
  ![Message](https://cdn.modrinth.com/data/A5foNgax/images/33ff6f43640a172fca1d49ee2052cba2db08523f.png)<br>


_Bstats from version 2.1_
[![BStats](https://bstats.org/signatures/bukkit/LoginTo.svg)](https://bstats.org/plugin/bukkit/LoginTo/28083)
