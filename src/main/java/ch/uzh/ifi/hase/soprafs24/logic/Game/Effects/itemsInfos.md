


## note about playerId:
- the playerIds are represented as strings
- the playerId `choice` is used if the player is specified by the frontend (the ids will be passed in an Array with the same order as the needed ids)
- the playerId of `current` is used for the player whos turn it is
- the playerId of `all` is used for all players
- the playerId of `others` is used for all players but the player whos turn it is
- the playerId of `enemy` is used for enemies
- the playerId of `team` is used for the teammate

## note about updatemoney
- if the giving amount is set to `all` a player will need to give all his money
- if the receiving amount is set to `givenAmount` all the money that is given will be divided to the receiving players

## note about exchange
- `player` parameter
    - for the player in the give parameter only a single player is allowed
    - if the value for the player in the get parameter is more than one player the give player may not give anything
    - if a player needs to exchange with multiple players use the effect multiple times
- `type` parameter
  - if no value set to empty string
- `selection` parameter
  - if no value set to empty String
  - can be value all
  - can be value choice
  - can be value random