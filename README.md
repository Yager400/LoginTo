![LoginTo](https://cdn.modrinth.com/data/cached_images/9d4250ca2166d5977633f4c456472eb6e43a178a.png)

## **What is LoginTo?**
  LoginTo is a fully customizable plugin for player autentication, this plugin have a fully personalization about any messages and a config file full of features to explore, use and change

## **Server type:**
  I tested this plugin in papermc 1.21 - 1.21.4 and purpur 1.21.4, the plugin use only bukkit and spigot api so it should on bukkit and spigot too
  (1.21.4 and 1.21.5 use the same bukkit/spigot api)

## **Commands:**
  ``/login <password>`` --> Sing up in your account<br>
  ``/register <password> <confirm_password>`` --> Register yourself to the server<br>
  ``/delacc <user>`` --> **Admin Only**  Delete a user account <br>
  ``/premium`` --> Login with premium minecraft instad of /login command<br>
  ``/changepassword <old_password> <new_password>`` --> Let a player to change his password<br>
  ``/loginto-reload`` --> Reload config, message and data without reloading or restarting the server<br>

## **Reload**
Do **NOT** use the command ``/reload confirm``, if you need to change dome settings/messages use **/loginto-reload** to refresh the plugin, or use **/stop** or **/restart** but **NOT /reload confirm**

## **News?**
Fixed a security issue with the premium feature <br>
Added the internal **reload command**: ``/loginto-reload``

## **Permission**
I tested those permission with **LuckPerms**

``loginto.register`` --> Permission for register command
``loginto.login`` --> Permission for login command
``loginto.delacc`` --> Permission for delacc command
``loginto.premium`` --> Permission for premium command
``loginto.changepassword`` --> Permission for changepassword command<br>
``loginto.reload`` --> Permission for loginto-reload command<br>


## **How to customize the text?**
  Go in the plugin folder, open the message.yml and change the message text

## **WARNING**
  The ``/premium`` command and the discord's webhooks uses an http request from mojang and discord

## **Next?**
  Fixing the velocity server switching
  
## **Social:**
  For anything that you need for this plugin you can join in too my [discord](https://discord.gg/Qmr22aaf4n)
