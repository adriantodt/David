# David
## Using the Official Version
(These 2 bots are being hosted by myself and are the most stable version you would even found)

`Current Stable Version: 3-LATEST`

[Invite David to your Guild](https://discordapp.com/oauth2/authorize?client_id=219162800516235275&scope=bot) (You should invite this one)

-----

`Current Development (This Branch) Version: 4.0-RETHINKTEST_1`

[Invite David Beta to your Guild](https://discordapp.com/oauth2/authorize?client_id=228629168231940096&scope=bot) (Only Online when testing new code; Can spam things often)

## Running (4.X+)
1. Start a [RethinkDB Server](https://www.rethinkdb.com/docs/install/) somewhere.
2. Build a Jar (See below)
3. Create in the same directory of the Jar a `Configs.json` file
4. Put something like this in the file:
```
{
  "token": "YOUR_BOT_TOKEN_HERE",
  "ownerID": "YOUR_DISCORD_ID_HERE",
  "hostname": "localhost",
  "port": 28015
}
```
5\. Now run the bot.

# Building
1. Clone the repository
2. Open the console on it ***(PowerShell if it's Windows!!)***
3. `./gradlew build`
4. Wait 5 minutes while Gradle does all the hard work
5. Grab the DavidBot-X.X.X-fat.jar
