


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
- `selection` parameter
  - if nothing or all are part of the transaction set to null
  - can be value choice
  - can be value random
- the first specified player is the `giver` the second specified player is the `getter`