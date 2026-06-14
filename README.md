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
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">
    Commands
  </summary>

  <details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer; margin-left: 10px">
Bukkit
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
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer; margin-left: 10px">
Velocity / Bungeecord
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

  <pre>/unregister &lt;player_name> &lt;confirm></pre>
  <p>Deletes a player's account.<br>
  <strong>Permission:</strong> OP (by default)</p>
  <hr>

  <pre>/changepassword &lt;oldPassword> &lt;newPassword></pre>
  <p>Changes your account password.</p>
  <hr>

  <pre>/premium</pre>
  <p>Sets your account as premium</p>
  <hr>

  <pre>/cracked</pre>
  <p>Sets your account as cracked (disables premium validation).</p>
  <hr>

  </div>
</details>
</details>

<details>
  <summary style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer;">
    Permissions
  </summary>

  <details style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer; margin-left: 10px">
    <summary>
      Bukkit
    </summary>
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
      <pre>loginto.otp</pre>
      <p>Allows players to use the <code>/otp</code> command.<br>
      <strong>Default:</strong> Everyone</p>
  </details>

<details style="background-color: #4a4a4a; border-radius: 5px; margin-top: 20px; cursor:pointer; margin-left: 10px">
    <summary>
      Velocity / Bungeecord
    </summary>
      <pre>loginto.register</pre>
      <p>Allows players to use the <code>/register</code> command.<br>
      <strong>Default:</strong> Everyone</p>
      <hr>
      <pre>loginto.login</pre>
      <p>Allows players to use the <code>/login</code> command.<br>
      <strong>Default:</strong> Everyone</p>
      <hr>
      <pre>loginto.unregister</pre>
      <p>Allows players to delete accounts using <code>/delacc</code>.<br>
      <strong>Default:</strong> No (requires luckperms)</p>
      <hr>
      <pre>loginto.changepassword</pre>
      <p>Allows players to change their password with <code>/changepassword</code>.<br>
      <strong>Default:</strong> Everyone</p>
      <hr>
      <pre>loginto.premium</pre>
      <p>Allows players to set their own account as premium.<br>
      <strong>Default:</strong> Everyone</p>
      <hr>
      <pre>loginto.cracked</pre>
      <p>Allows players to switch their account to cracked mode.<br>
      <strong>Default:</strong> Everyone</p>
      <hr>
  </details>
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