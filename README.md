![LoginTo](https://cdn.modrinth.com/data/cached_images/9d4250ca2166d5977633f4c456472eb6e43a178a.png)



## **What is LoginTo?**

 LoginTo is a fully customizable plugin for player autentication, this plugin have a fully personalization about any messages and a config file full of features to explore, use and change



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

 ``/cracked`` -> Lock the cracked account, forcing the LoginTo authentication even if the 

 



## **News**

**Version 2.4**<br>

Added premium authentication via mojang for premium accounts (if he wants) and normal authentication via login for the cracked and bedrock ones<br>



Added velocity and bungeecord support for the premium authentication<br>



Added 3 new commands:<br>

- ``/premium`` -> Lock your account from cracked user, when this command it's used LoginTo will use the auto-login, and the velocity/bungee plugin won't let cracked account with the same name pass<br>

- ``/premium <user>`` -> ADMIN ONLY: set the status to an account<br>

- ``/cracked`` -> Lock the cracked account, forcing the LoginTo authentication even if the username became premium (can be removed with /premium or /delacc)<br>



Added the permission for the new commands:<br>

- ``loginto.premium.me`` -> The permission for **using the /premium command,** this is set to everyone by default (but you can change with luckperms)<br>

- ``loginto.premium.other`` -> The permission for **making an account premium**, this is set to only OP by default<br>

- ``loginto.cracked.me`` -> The permission for **using the /cracked** command, this is set to everyone by default<br>



Added a new listener, now if someone opens a world inventory (chest, furnace exc…) if the player is not logged the event will be cancelled<br>



Added **H2** database support (embedded) for bukkit<br>



Changed database connection to hikari (for more stability and security)<br>



Changed the update checker, now it will check for an update every hour, and not only when you startup your server<br>



**Fixed /delacc** command, now it delete the selected player's account<br>

Fixed the bStats metric "storage_type_used", now it works<br>



**Removed the "Go to another server" feature** for now<br>



## **Permission**

I tested those permission with **LuckPerms**



``loginto.register`` --> Permission for register command<br>

``loginto.login`` --> Permission for login command<br>

``loginto.delacc`` --> Permission for delacc command<br>

``loginto.changepassword`` --> Permission for changepassword command<br>

``loginto.premium.me`` -> The permission for **using the /premium command,** this is set to everyone by default (but you can change with luckperms)<br>

``loginto.premium.other`` -> The permission for **making an account premium**, this is set to only OP by default<br>

``loginto.cracked.me`` -> The permission for **using the /cracked** command, this is set to everyone by default<br>



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

