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

**Version 2.6**<br>
___

Added the listener **onDropItem**, now if someone is not logged in, he won't be able to drop any item

___

Added the teleportation to the old location after the login, now if the player finish the login, he will be teleported where he was

___

Fixed the /login spam bug, now if a player is already logged in, he won't be able to execute again the /login command

___

Fixed the coordinates leek problem, now the player position will get changed when he leave

___

Fixed **WebHooks**, and now they work better than before :)

___

Added custom WebHooks messages

___

Reduced the plugin file size by ~24Mb total by using libby, now you can share this in more social online like discord

___

Changed the way for updating the config in the bukkit plugin, this change was done since it can cause bugs and an ungly config

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
