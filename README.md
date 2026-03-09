## CLI
commands:
```text
> help
exit
help
data <subcommand> <args>
     import <filename>
     export <filename>
guest <subcommand> <args>
      create <name>
      get <id>
      list
      delete <id>
      set-name <id> <name>
hotel <subcommand> <args>
      create <name>
      get <id>
      list
      delete <id>
      set-name <id> <name>
      <hotel-id> <subcommand> <args>
                  occupancy <date-from>, <date-in>
                  revenue <date-from>, <date-in>
      <hotel-id> room <subcommand> <args>
                       add <in-hotel-room-id>, <type>, <price>
                       get <in-hotel-room-id>
                       delete <in-hotel-room-id>
                       set-price <id> <price>
                       <in-hotel-room-id> occupancy <date-from>, <date-in>
                       <in-hotel-room-id> revenue <date-from>, <date-in>
reservation <subcommand> <args>
            available <hotel-id>, <in-date>, <out-date>
            make <hotel-id>, <in-hotel-room-id>, <guest-id>, <in-date>, <out-date>
            get <id>
            cancel <id>
```