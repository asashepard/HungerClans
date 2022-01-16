# HungerClans

Clans plugin for PaperMC to be used with ItsGJK/gabrieljkeller's HungerCore.
Create and customize "clans" within a server running PaperMC.

Commands:
- /clan OR /c
- /c help
- /c create [clan name]
- /c list
- /c members OR /c members [clan name]
- /c color
- /c banner
- /c [promote|demote|kick|invite] [player]
- /c leave
- /clanwhisper OR /clanmessage OR /cw OR /cmsg OR /ctell [message...]
- /c sethome
- /c home
- /war
- /war all
- /surrender
- /clanconfig OR /cconfig [key] [value]

Features:
- "Clans"
  - Roles: member, trusted, leader
  - Color
  - Banner
  - Motto
  - Clan Home
  - Points System
- "Wars"
  - Score System
  - Rewards through VaultAPI
- Nametag configuration through NametagAPI
- Configuration options (see /src/main/resources/config.yml)
  - Configurable daily rewards
  - Configurable combat log detection and punishment
