### Version 3.7.0

___

Added cool minimessage (no more legacy), now it supports gradient and rgb (only for client and servers over 1.16)

___

Fully redesigned how the plugin in the proxy works

___

Removed the premium player's bridge between the proxy and bukkit.<br>
Why?, the classic premium feature was too complex to set up.<br> Now if you want to use the plugin in a network, you install it only on the proxy. <br> If you don't have a network, you can still use the premium feature with just bukkit (not this variant of the premium feature is no more an experimental feature)

___

/premium and /cracked command now work for the bukkit premium feature

___

**Important**<br>
Changed the password's column name from 'password_hash' to 'password' for H2 databases, this will break every old H2 database, but from the bstats data, no one was using it so it's ok