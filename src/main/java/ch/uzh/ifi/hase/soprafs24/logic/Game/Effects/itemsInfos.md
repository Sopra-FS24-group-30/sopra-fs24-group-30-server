

# ItemInfos
This is the documentation on how to specify effects

## givePlayerDice
this effect throws several dice and can grant a bonus pay if enough dice match
- parameter dice:
  - Integer which is the number of dice to be rolled (can be saved as string)
- parameter bonusCount:
  - Integer which is the number of dice which need to match for the player to get the bonus cash
- parameter money;
  - Integer which is the number of Cash the player gets in case of a sufficient pash

## updateMoney
this effect lets players get or pay money
- parameter type:
  - String which is either `absolute` or `relative` it defines how cash is added / subtracted
- parameter amount:
  - JSONObject containing:
    - playerIds as keys
    - a value to be added to the players cash as value
      - note if type is absolute the value can be any Integer
      - if the type is relative it has to be `-100 <= x <= 100`

## exchange
this effect lets players exchange items and cards (note both players give and get items, to avoid confusion keep the giver as the person initiating the item/ultimate)
- parameter give
  - JSONObject representing the giving player
    - key "player" : playerId which is the giving player
    - key "type" : either `card` or `item` depending on wheter you want to give cards or items
    - key "selection" : a SELECTION to define how the items/cards are selected
    - key "amount" : Integer how many items / cards are to be given
- parameter get
  - works the same way but representing the getting player

## updatePositions

{"player": "current", "field": "randomPlayer"}},

## givePlayerCardRand

## givePlayerCardChoice

## exchangePositions

## reduceMoneyALL

## changeGoalPosition

## rechargeUlt

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