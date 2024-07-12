<div align="center">
    <img src="https://i.imgur.com/gICAaX6.gif">
    <h1>HologramChat</h1>
</div>

<h2>Overview</h2>
<p>HologramChat is a Minecraft Bukkit/Spigot/Paper plugin designed to display chat messages as holograms above players' heads. This plugin enhances server communication by providing a visual representation of in-game chat messages.</p>

<h2>Features</h2>
<ul>
    <li>Displays chat messages as holograms above players' heads</li>
    <li>Customizable hologram formatting and positioning</li>
    <li>Supports placeholders and formatting codes</li>
    <li>Toggle visibility of specific messages (example. /msg) only displays to the 2 participants.</li>
    <li>Hologram follows the player</li>
</ul>

<div align="center">
    <h2>Spoiler: Config.yml</h2>
</div>

<pre><code class="yaml">
# The vertical offset for the messages (height above the player's head)
yOffset: 0.0
textFollowUpdateInterval: 1
displayDuration: 3
lineLength: 30
increaseDisplayDurationPerLine: 1
radius: 50
disabledWorlds:
  - "custom_world_1"
  - "custom_world_2"
seeOwnMessageHologram: true
groups:
  example:
    messageFormat: "&7EXAMPLEMESSAGE: &f<message> [HP: %player_health%]"
    permission: "hologramchat.example"
    weight: 10
  default:
    messageFormat: "#C0C0C0<message>"
    permission: "hologramchat.default"
    weight: 20
  staff:
    messageFormat: "&7[&4STAFF&7] #FF8000<message>"
    permission: "hologramchat.staff"
    weight: 30
</code></pre>

<div align="center">
    <h2>MUST BE INSTALLED TO WORK:</h2>
    <p><a href="https://www.spigotmc.org/resources/protocollib.1997/">Protocollib</a></p>
    <h2>OPTIONAL:</h2>
    <p><a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI (for adding placeholders)</a></p>
    <p><a href="https://essentialsx.net/downloads.html">EssentialsX (for private messages)</a></p>
</div>

<h2>Commands</h2>
<table>
    <thead>
        <tr>
            <th>Command</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>/hologramchatreload</td>
            <td>Reloads the HologramChat configuration files.</td>
        </tr>
    </tbody>
</table>

<h2>Permissions</h2>
<table>
    <thead>
        <tr>
            <th>Permission</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>hologramchat.default</td>
            <td>Gives the messageFormat of default</td>
        </tr>
        <tr>
            <td>hologramchat.staff</td>
            <td>Gives the messageFormat of staff</td>
        </tr>
        <tr>
            <td>hologramchat.&lt;preference&gt;</td>
            <td>Define your own permissions and groups</td>
        </tr>
        <tr>
            <td>hologramchat.admin</td>
            <td>Grants access to administrative commands.</td>
        </tr>
    </tbody>
</table>

<div align="center">
    <h2>Please report bugs before posting a negative review.</h2>
    <p><a href="https://github.com/Niko302/HologramChat/issues">https://github.com/Niko302/HologramChat/issues</a></p>
    <h2>Give me a ⭐ ⭐ ⭐ ⭐ ⭐ review if satisfied</h2>
</div>
