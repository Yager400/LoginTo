### Version 3.5.0

___

Fixed delacc command problem

___

Deprecated **delacc** command and changing it to **unregister** command, changed the permission for the command from **loginto.delacc** to **loginto.unregister**, added the aliases **delacc** for **unregister** command

___

Added minimessages support (legacy)

___

Added authentication via app (like google authenticator, microsoft authenticator and other) with the 6 numeric digit code (OTP)

___

Changed how the /changepassword command works (now it uses the otp code for changing the password)

___

Added /otp command for getting the otp code to change the password

___

Added permission for /otp command: <b>loginto.otp</b>

___

Fixed vehicle despawn bug, now when a player leave the server while riding a vehicle, it will no more get despawned

___

Added banned password (and )

___

Changed build system to gradle

___

Plugin Versioning System

Format: **`MAJOR.MINOR.PATCH`**

* **`X.0.0` | Major Release**
  * Major structural changes, rewrites, or breaking changes
* **`0.X.0` | Minor Release**
  * New features and improvements
* **`0.0.X` | Patch Release**
  * Bug fixes, security patches, and minor optimizations