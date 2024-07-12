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
    <button onclick="toggleSpoiler('config')">Spoiler: Config.yml</button>
    <div id="configSpoiler" style="display: none;">
        <pre><code class="yaml">
# The vertical offset for the messages (height above the player's head)
yOffset: 0.0
# How often (in ticks) the text following updates. 1 tick = 1/20 second
textFollowUpdateInterval: 1
# The base duration (in seconds) that the messages will be displayed
displayDuration: 3
# The maximum length of each line in the message
lineLength: 30
# The additional duration (in seconds) to display the message per line
increaseDisplayDurationPerLine: 1
# The radius around the player in which the messages will be displayed
radius: 50
# A list of worlds where the chat bubbles are disabled
disabledWorlds:
  - "custom_world_1"
  - "custom_world_2"
# Option to see your own message hologram
seeOwnMessageHologram: true

# Message groups configuration
groups:
  # Default group configuration
  example:
    # The format of the message
    messageFormat: "&7EXAMPLEMESSAGE: &f<message> [HP: %player_health%]"
    # The permission required for this message format
    permission: "hologramchat.example"
    # The weight of this group (higher values take precedence)
    weight: 10

  # Default group configuration
  default:
    # The format of the message
    messageFormat: "#C0C0C0<message>"
    # The permission required for this message format
    permission: "hologramchat.default"
    # The weight of this group (higher values take precedence)
    weight: 20

  # Staff group configuration
  staff:
    # The format of the message
    messageFormat: "&7[&4STAFF&7] #FF8000<message>"
    # The permission required for this message format
    permission: "hologramchat.staff"
    # The weight of this group (higher values take precedence)
    weight: 30
        </code></pre>
    </div>
</div>

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

<script>
    function toggleSpoiler(spoilerId) {
        var spoiler = document.getElementById(spoilerId + 'Spoiler');
        if (spoiler.style.display === 'none') {
            spoiler.style.display = 'block';
        } else {
            spoiler.style.display = 'none';
        }
    }
</script>
