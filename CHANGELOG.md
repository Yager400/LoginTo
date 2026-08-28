### Version 4.0.0

Rewrote major of the plugin (like 80% of it)

___

Optimized nearly everything by making a lot of functions async

___

Fully changed the config.yml and messages.yml, now those are easier to modify

___

Added webhook.yml for a better management of the discord's webhooks

___

Added sessions, now if a player leaves the server and rejoins back with the same name and ip in a short amount of time, they won't need to re authenticate them self

___

Changed the way data get stored, now the player's position (for bukkit) is stored in the database

___

Added the "_pluginData" folder to separate what the server owner should modify and what is reserved to the plugin

___

Removed the logs feature

___

Fixed the database problem on the proxies

___

Made the bungee/velocity ann bukkit version of the plugin much more similar

___

Added Folia support

___

Changed the required java version to 17 instead of 8

___

Changed the minimum bukkit version from 1.13 to 1.18

___

Changed how the otp code get shown to the player, now instead of a separated world (witch will cause lag), the player will receive a map with that qrcode

___

Changed the package name from net.loginto to com.github.yager400.loginto for (probably) a future api

___

Added title support for the messages with the tag <title> and <subtitle>

___

Added a bridge between proxy and bukkit, now even velocity or bungeecord can blind the player, hide their inventory and stop them from moving