# How the translation system works
## Part 0. If you don't want to read me trying (and failing) to explain how it works:
1. I recommend getting yourself an IDE/Syntax Highlighter. JSON can trick your eyes in a blink. IntelliJ or Notepad++ goes fine.
2. Edit only the values (`"dontEditThis":"editTHIS"`)
3. Submit a PR
4. I'll probably accept

## Part 1. Files:
### File 1. Main.json:
The `main.json` file have the sole purpose to tell the bot: `"Hey there is a language over there go read it"`.

How the bot reads it:
```js
{
  "en_US": {}, // Default constructor, no params
  "pt_BR": {
    "parent": "en_US" // Defines the language with the parent being "en_US"
  },
  ...
}
```

Then, it'll search for ***language_id***.json (en_US.json, pt_BR.json, etc) and if the file exists, load it.

### File 2. your_language_id_here.json
Every language file is structured the following:
```js
{
  "translations": {...},
  "meta": {...},
  "commands": {...}
}
```

The `translations` entries are used for not command-related, while the `commands` entries are used for command-related, and the `meta` entries actually don't get loaded to the bot, but are used to generate translations

Is somewhat confusing but I'll try to explain:

#### Translations
Translations follow a somewhat simple pattern:
```js
  "translations": {
    "tree": {
      "subcmds": "Sub-Commands", // This will register "tree.subcmds" -> "Sub-Commands"
      "default": "default" // This will register "tree.default" -> "default"
    },
    "alias.of": "Alias of %s", // This will register "alias.of" -> "Alias of %s"
  }
```
It's a basic register that accepts both a tree-scheme and a simple-scheme.

### Meta
Currently the `meta` is used to generate the commands usage:
```js
  "meta": { //Nothing in this will be registered, but the commands later will use
    "params": "Parameters",
    "noParams": "none",
    "noDesc": "(No Description Provided)"
  }
```
So yeah, there isn't much to tell about.

### Commands
This is where things start to be weird:
```
  "commands": {
    "bot": {
      "subs": {
        "inviteme": {
          "desc": "Sends an Link to Invite the Bot to your Guild",
          "params": "[none]",
          "info": "You need to have the MANAGE SERVER permission on the server you want to put the bot.",
          "translations": {
            "link": "Click the Link to invite the Bot to your Guild"
          }
        }
      },
      "translations": {
       "help": "Hello! I'm $(dynamic.botname).\nTo get started with the Commands, send: $(dynamic.mention) cmds\nTo invite me to your Guild, send: $(dynamic.mention) bot inviteme"
      }
    }
  }
```

In this part, a `command` *child* tag can have 3 parts:
1. Usage definition (`desc`,`info` and/or `params`, being the last two optional).
2. Translations (`translations`), where `commands.bot.translations.help` will be converted to `bot.help` -> value.
3. Sub-Commands (`subs`), so it's going to generate the child commands that you may encounter in the bot.



