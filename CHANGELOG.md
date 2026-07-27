### Version 3.8.0

Fixed a permission problem with /cracked command

___

Added an option to let cracked accounts bypass the premium authentication even if their username is premium.<br>
However they will still need to do **/login password** before playing into the server

___

Optimized the player's movement event, not it will only block the player moving, not the camera moving

___

Fixed the /otp command problem where, on newer version, it will show an error due to gamerules changes

___

Fixed an error popping out when the /unregister command was executed on yourself (this didn't cause the plugin not to work, but wan annoying to see that error in the logs)